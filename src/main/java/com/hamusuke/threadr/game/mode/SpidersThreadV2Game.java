package com.hamusuke.threadr.game.mode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hamusuke.threadr.game.card.ServerCard;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class SpidersThreadV2Game {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Byte> ALL_CARDS = Util.make(() -> {
        var list = Lists.<Byte>newArrayList();
        IntStream.rangeClosed(1, 100).forEach(value -> list.add((byte) value));
        return ImmutableList.copyOf(list);
    });
    protected final Set<Byte> givenNum = Sets.newHashSet();
    protected final Random random = new Random();
    protected Status status = Status.NONE;
    protected final List<ServerSpider> spiders;
    protected final ThreadRainbowServer server;
    protected Topic topic;
    protected final List<Integer> cards = Collections.synchronizedList(Lists.newArrayList());
    protected int uncoveredIndex;
    protected boolean failed;
    protected boolean succeeded;

    public SpidersThreadV2Game(ThreadRainbowServer server, List<ServerSpider> spidersToPlay) {
        this.server = server;
        this.spiders = Collections.synchronizedList(Lists.newArrayList(spidersToPlay));
    }

    public void tick() {
    }

    public void start() {
        if (this.status == Status.NONE) {
            this.nextStatus();
            this.giveOutCards();
        }
    }

    protected void giveOutCards() {
        if (this.status != Status.GIVING_OUT_CARDS) {
            return;
        }

        this.spiders.forEach(spider -> {
            var remaining = ALL_CARDS.stream().filter(integer -> !this.givenNum.contains(integer)).toList();
            byte num = Util.chooseRandom(remaining, this.random);
            this.givenNum.add(num);
            spider.takeCard(new ServerCard(spider, num));
            spider.sendPacket(new LocalCardHandedNotify(num));
        });
        this.spiders.forEach(spider -> this.sendPacketToOthersInGame(spider, new RemoteCardGivenNotify(spider)));
        this.nextStatus();
    }

    public void startTopicSelection() {
        if (this.status != Status.CHECKING_NUMBER) {
            return;
        }

        this.nextStatus();
        this.chooseRandomTopic();
        this.sendPacketToAllInGame(new StartTopicSelectionNotify(this.topic));
    }

    public void changeTopic() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        this.chooseRandomTopic();
        this.sendPacketToAllInGame(new TopicChangeNotify(this.topic));
    }

    protected void chooseRandomTopic() {
        var topics = this.server.getTopicLoader().getTopics();
        this.topic = Util.chooseRandom(topics, this.random);
    }

    public void decideTopic() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        this.nextStatus();
        this.preLineup();
        this.spiders.forEach(spider -> {
            spider.sendPacket(new ChatNotify("お題が決まりました"));
            spider.sendPacket(new ChatNotify("お題に沿って「たとえ」て小さい順に並べましょう"));
            spider.sendPacket(new StartMainGameNotify(this.cards));
        });
    }

    protected void preLineup() {
        this.cards.clear();
        this.spiders.forEach(spider -> this.cards.add(spider.getId()));
    }

    public synchronized void moveCard(ServerSpider operator, int from, int to) {
        if (this.status != Status.PLAYING) {
            return;
        }

        if (!this.spiders.contains(operator)) {
            operator.sendError("カードを動かせません");
            return;
        }

        try {
            var v = this.cards.get(from);
            if (to < from) {
                this.cards.remove(from);
                this.cards.add(to, v);
            } else {
                this.cards.add(to + 1, v);
                this.cards.remove(from);
            }

            this.sendPacketToAllInGame(new CardMoveNotify(from, to));
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn("The card moved wrongly!", e);
            operator.sendError("カードが変なところに移動したので操作をキャンセルしました");
        }
    }

    public void finish() {
        if (this.status != Status.PLAYING) {
            return;
        }

        this.nextStatus();
        this.sendPacketToAllInGame(new FinishMainGameNotify());
    }

    public void uncover() {
        if (this.status == Status.FINISH) {
            this.nextStatus();
        }

        if (this.status != Status.RESULT) {
            return;
        }

        if (this.uncoveredIndex >= this.cards.size()) {
            LOGGER.warn("Uncovering unknown card: {}", this.uncoveredIndex);
            return;
        }

        int cur = this.uncoveredIndex;
        int ownerId = this.cards.get(this.uncoveredIndex++);
        int prevId = cur > 0 ? this.cards.get(cur - 1) : -1;
        ServerSpider owner = null;
        ServerSpider prev = null;
        for (ServerSpider spider : this.spiders) {
            if (spider.getId() == ownerId) {
                owner = spider;
            } else if (spider.getId() == prevId) {
                prev = spider;
            }
        }

        if (cur > 0) {
            if (owner != null && prev != null && owner.getHoldingCard().getNumber() < prev.getHoldingCard().getNumber()) {
                this.fail();
            }
        }

        boolean last = this.cards.size() <= this.uncoveredIndex;
        if (owner != null) {
            this.sendPacketToAllInGame(new UncoverCardNotify(owner.getId(), owner.getHoldingCard().getNumber(), last));
        }
        if (last && !this.failed) {
            this.succeed();
        }
    }

    protected void fail() {
        if (this.failed) {
            return;
        }

        this.failed = true;
        this.sendPacketToAllInGame(new ChatNotify("失敗です！もう一度挑戦してみましょう"));
    }

    protected void succeed() {
        if (this.failed || this.succeeded) {
            return;
        }

        this.succeeded = true;
        this.sendPacketToAllInGame(new ChatNotify("成功です！"));
    }

    public void restart() {
        if (this.status != Status.RESULT) {
            return;
        }

        this.nextStatus();
        this.server.restartGame();
    }

    protected void nextStatus() {
        if (this.status == Status.END) {
            return;
        }

        this.status = this.status.next();
    }

    public List<ServerSpider> getPlayingSpiders() {
        return ImmutableList.copyOf(this.spiders);
    }

    public synchronized void onSpiderLeft(ServerSpider spider) {
        this.spiders.remove(spider);
        this.cards.removeIf(integer -> spider.getId() == integer);
        this.finishIfLastCardLeft();
        /*
        if (this.server.isHost(spider) && !this.spiders.isEmpty()) {
            this.server.getSpiderManager().changeHost(this.spiders.get(0));
        }

         */
        this.sendPacketToAllInGame(new SpiderExitGameNotify(spider));
        this.sendPacketToAllInGame(new ChatNotify(spider.getName() + " がゲームをやめました"));
    }

    private void finishIfLastCardLeft() {
        if (this.status != Status.RESULT || this.cards.size() > this.uncoveredIndex || this.succeeded) {
            return;
        }

        this.sendPacketToAllInGame(new UncoverCardNotify(-1, (byte) -1, true));
        if (!this.failed) {
            this.succeed();
        }
    }

    public void sendPacketToAllInGame(Packet<?> packet) {
        this.spiders.forEach(spider -> spider.sendPacket(packet));
    }

    protected void sendPacketToOthersInGame(ServerSpider exclusive, Packet<?> packet) {
        this.spiders.stream().filter(spider -> !spider.equals(exclusive)).forEach(spider -> spider.sendPacket(packet));
    }

    protected enum Status {
        NONE,
        GIVING_OUT_CARDS,
        CHECKING_NUMBER,
        SELECTING_TOPIC,
        PLAYING,
        FINISH,
        RESULT,
        END;

        private Status next() {
            int i = this.ordinal() + 1;
            return i >= Status.values().length ? NONE : Status.values()[i];
        }
    }
}
