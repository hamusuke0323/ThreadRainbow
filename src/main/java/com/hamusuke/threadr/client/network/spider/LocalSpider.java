package com.hamusuke.threadr.client.network.spider;

import com.hamusuke.threadr.client.game.card.LocalCard;

public class LocalSpider extends AbstractClientSpider {
    private LocalCard localCard;

    public LocalSpider(String name) {
        super(name);
    }

    public void takeCard(LocalCard localCard) {
        this.localCard = localCard;
    }

    public LocalCard getLocalCard() {
        return this.localCard;
    }
}
