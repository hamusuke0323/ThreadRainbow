package com.hamusuke.threadr.client.game.card;

import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.network.Spider;

public class RemoteCard extends AbstractClientCard {
    private final RemoteSpider owner;
    private byte number = -1;

    public RemoteCard(RemoteSpider owner) {
        this.owner = owner;
    }

    @Override
    public void setNumber(byte number) {
        this.number = number;
    }

    @Override
    public Spider getOwner() {
        return this.owner;
    }

    @Override
    public byte getNumber() {
        return this.number;
    }

    @Override
    public boolean canBeSeen() {
        return this.number > 0;
    }
}
