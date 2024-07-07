package com.hamusuke.threadr.client.game.card;

import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.network.Spider;

public class LocalCard extends AbstractClientCard {
    private final LocalSpider owner;
    private final byte num;

    public LocalCard(LocalSpider owner, byte num) {
        this.owner = owner;
        this.num = num;
    }

    @Override
    public Spider getOwner() {
        return this.owner;
    }

    @Override
    public byte getNumber() {
        return this.num;
    }

    @Override
    public boolean canBeSeen() {
        return true;
    }
}
