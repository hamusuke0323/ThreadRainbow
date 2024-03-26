package com.hamusuke.threadr.game.card;

import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.network.Spider;

public class LocalCard implements NumberCard {
    private final LocalSpider owner;
    private final byte num;
    private boolean uncovered;

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

    @Override
    public boolean isUncovered() {
        return this.uncovered;
    }

    public void uncover() {
        this.uncovered = true;
    }
}
