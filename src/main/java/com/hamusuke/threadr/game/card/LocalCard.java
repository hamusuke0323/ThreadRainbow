package com.hamusuke.threadr.game.card;

import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.network.Spider;

public record LocalCard(LocalSpider owner, byte num) implements NumberCard {
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
