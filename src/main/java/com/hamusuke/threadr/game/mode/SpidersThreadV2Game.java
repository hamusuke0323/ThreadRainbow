package com.hamusuke.threadr.game.mode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.game.card.ServerCard;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.room.ServerRoom;
import com.hamusuke.threadr.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
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
    protected final SecureRandom random = new SecureRandom();
    protected Status status = Status.NONE;
    protected final List<ServerSpider> spiders;
    protected final ThreadRainbowServer server;
    protected final ServerRoom room;
    protected TopicEntry topic;
    protected final List<Integer> cards = Collections.synchronizedList(Lists.newArrayList());
    protected int uncoveredIndex;
    protected boolean failed;
    protected boolean succeeded;

    public SpidersThreadV2Game(ThreadRainbowServer server, ServerRoom room, List<ServerSpider> spidersToPlay) {
        this.server = server;
        this.room = room;
        this.spiders = Collections.synchronizedList(Lists.newArrayList(spidersToPlay));
    }

    public void tick() {
    }

    public void start() {
        if (this.status == Status.NONE) {
            this.sendPacketToAllInGame(new ChatNotify("ゲームを開始します"));
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
        this.sendPacketToAllInGame(new ChatNotify("カードを配りました"));
        this.nextStatus();
    }

    public void startTopicSelection() {
        if (this.status != Status.CHECKING_NUMBER) {
            return;
        }

        if (this.chooseRandomTopic()) {
            return;
        }

        this.nextStatus();
        this.sendPacketToAllInGame(new StartTopicSelectionNotify(this.topic.id()));
        this.sendPacketToAllInGame(new ChatNotify("ホストはお題を再度選ぶこともできます"));
    }

    public void changeTopic() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        if (this.chooseRandomTopic()) {
            return;
        }

        this.sendPacketToAllInGame(new TopicChangeNotify(this.topic.id()));
    }

    protected boolean chooseRandomTopic() {
        var topics = this.room.getTopicList().getTopicEntries();
        if (topics.isEmpty()) {
            this.sendPacketToAllInGame(new ChatNotify("この部屋で利用可能なお題がありません"));
            this.sendPacketToAllInGame(new ChatNotify("ホストはお題を作成できます"));
            return true;
        }

        this.topic = Util.chooseRandom(topics, this.random);
        return false;
    }

    public boolean setTopic(int topicId) {
        if (this.status != Status.SELECTING_TOPIC) {
            return true;
        }

        var topics = this.room.getTopics();
        if (!topics.containsKey(topicId)) {
            return false;
        }

        this.topic = topics.get(topicId);
        this.sendPacketToAllInGame(new TopicChangeNotify(this.topic.id()));
        return true;
    }

    public void decideTopic() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        this.preLineup();
        this.spiders.forEach(spider -> {
            spider.sendPacket(new ChatNotify("お題が決まりました"));
            spider.sendPacket(new ChatNotify("お題に沿って「たとえ」て小さい順に並べましょう"));
            spider.sendPacket(new StartMainGameNotify(this.cards));
        });

        this.nextStatus();
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
        this.sendPacketToAllInGame(new ChatNotify("完成！"));
        this.sendPacketToAllInGame(new ChatNotify("ホストはカードをめくってください"));
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
        this.nextStatus();
        this.sendPacketToAllInGame(new ChatNotify("失敗です！もう一度挑戦してみましょう"));
    }

    protected void succeed() {
        if (this.failed || this.succeeded) {
            return;
        }

        this.succeeded = true;
        this.nextStatus();
        this.sendPacketToAllInGame(new ChatNotify("成功です！"));
    }

    public void restart() {
        if (this.status != Status.END) {
            return;
        }

        this.room.restartGame();
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
        if (!this.spiders.contains(spider)) {
            return;
        }

        this.spiders.remove(spider);
        if (this.spiders.isEmpty()) {
            this.room.endGame();
            return;
        }

        this.cards.removeIf(integer -> spider.getId() == integer);
        this.finishIfLastCardLeft();
        if (this.room.isHost(spider) && !this.spiders.isEmpty()) {
            this.room.changeHost(this.spiders.get(0));
        }

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
