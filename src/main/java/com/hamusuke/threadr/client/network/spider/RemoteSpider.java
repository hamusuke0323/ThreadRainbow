package com.hamusuke.threadr.client.network.spider;

import com.hamusuke.threadr.client.game.card.RemoteCard;

public class RemoteSpider extends AbstractClientSpider {
    private RemoteCard remoteCard;

    public RemoteSpider(String name) {
        super(name);
    }

    public void haveRemoteCard(RemoteCard remoteCard) {
        this.remoteCard = remoteCard;
    }

    public RemoteCard getRemoteCard() {
        return this.remoteCard;
    }
}
