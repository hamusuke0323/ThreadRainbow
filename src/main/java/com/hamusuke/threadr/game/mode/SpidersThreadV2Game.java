package com.hamusuke.threadr.game.mode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.GiveLocalCardS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.RemoteCardGivenS2CPacket;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.util.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class SpidersThreadV2Game {
    private static final List<Byte> ALL_CARDS = Util.make(() -> {
        var list = Lists.<Byte>newArrayList();
        IntStream.rangeClosed(1, 100).forEach(value -> list.add((byte) value));
        return ImmutableList.copyOf(list);
    });
    protected final Set<Byte> givenNum = Sets.newHashSet();
    protected Status status = Status.NONE;
    protected final List<ServerSpider> spiders;
    protected final ThreadRainbowServer server;

    public SpidersThreadV2Game(ThreadRainbowServer server, List<ServerSpider> spidersToPlay) {
        this.server = server;
        this.spiders = spidersToPlay;
    }

    public void tick() {
        if (this.status == Status.NONE) {
            return;
        }


    }

    public void start() {
        if (this.status == Status.NONE) {
            this.nextStatus();
            this.giveOutCards();
        }
    }

    protected void giveOutCards() {
        if (this.status != Status.GIVE_OUT_CARDS) {
            return;
        }

        this.nextStatus();
        this.spiders.forEach(spider -> {
            var remaining = ALL_CARDS.stream().filter(integer -> !this.givenNum.contains(integer)).toList();
            byte num = Util.chooseRandom(remaining, this.server.getRandom());
            this.givenNum.add(num);
            spider.takeCard(new NumberCard(spider, num));
            spider.sendPacket(new GiveLocalCardS2CPacket(num));
            spider.sendPacketToOthers(new RemoteCardGivenS2CPacket(spider));
        });
    }

    protected void nextStatus() {
        if (this.status == Status.RESULT) {
            return;
        }

        this.status = this.status.next();
    }

    public List<ServerSpider> getPlayingSpiders() {
        return this.spiders;
    }

    private enum Status {
        NONE,
        GIVE_OUT_CARDS,
        CHECK_NUMBER,
        SELECT_TOPIC,
        PLAY,
        FINISH,
        RESULT;

        private Status next() {
            int i = this.ordinal() + 1;
            return i >= Status.values().length ? NONE : Status.values()[i];
        }
    }
}
