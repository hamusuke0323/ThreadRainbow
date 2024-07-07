package com.hamusuke.threadr.client.game.card;

import com.hamusuke.threadr.game.card.NumberCard;

public abstract class AbstractClientCard implements NumberCard {
    protected boolean out;
    protected boolean uncovered;

    @Override
    public boolean isOut() {
        return this.out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    @Override
    public boolean isUncovered() {
        return this.uncovered;
    }

    public void uncover() {
        this.uncovered = true;
    }

    @Override
    public String toString() {
        return this.canBeSeen() ? Byte.toString(this.getNumber()) : "???";
    }
}
