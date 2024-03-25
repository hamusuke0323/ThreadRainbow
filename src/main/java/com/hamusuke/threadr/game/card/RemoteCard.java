package com.hamusuke.threadr.game.card;

import com.hamusuke.threadr.client.network.spider.RemoteSpider;

public class RemoteCard {
    private final RemoteSpider owner;
    private byte number = -1;

    public RemoteCard(RemoteSpider owner) {
        this.owner = owner;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public byte getNumber() {
        return this.number;
    }

    public boolean canBeSeen() {
        return this.number > 0;
    }
}
