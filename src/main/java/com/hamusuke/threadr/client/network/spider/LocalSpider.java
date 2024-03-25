package com.hamusuke.threadr.client.network.spider;

import com.hamusuke.threadr.game.card.NumberCard;

public class LocalSpider extends AbstractClientSpider {
    private NumberCard localCard;

    public LocalSpider(String name) {
        super(name);
    }

    public void takeCard(NumberCard localCard) {
        this.localCard = localCard;
    }

    public NumberCard getLocalCard() {
        return this.localCard;
    }
}
