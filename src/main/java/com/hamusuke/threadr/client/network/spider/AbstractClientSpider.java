package com.hamusuke.threadr.client.network.spider;

import com.hamusuke.threadr.client.game.card.AbstractClientCard;
import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.network.Spider;

import javax.annotation.Nullable;

public abstract class AbstractClientSpider extends Spider {
    @Nullable
    public TeamType myTeam;
    @Nullable
    private AbstractClientCard clientCard;

    protected AbstractClientSpider(String name) {
        super(name);
    }

    @Nullable
    public AbstractClientCard getClientCard() {
        return this.clientCard;
    }

    public void setClientCard(@Nullable AbstractClientCard clientCard) {
        this.clientCard = clientCard;
    }

    public void setId(int id) {
        this.id = id;
    }
}
