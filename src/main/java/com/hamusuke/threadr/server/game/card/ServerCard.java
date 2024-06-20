package com.hamusuke.threadr.server.game.card;

import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.server.network.ServerSpider;

public record ServerCard(ServerSpider owner, byte num) implements NumberCard {
    @Override
    public Spider getOwner() {
        return this.owner;
    }

    @Override
    public byte getNumber() {
        return this.num;
    }
}
