package com.hamusuke.threadr.game.card;

import com.hamusuke.threadr.network.Spider;

public interface NumberCard {
    Spider getOwner();

    byte getNumber();

    default boolean canBeSeen() {
        return false;
    }
}
