package com.hamusuke.threadr.game.card;

import com.hamusuke.threadr.network.Spider;

public interface NumberCard {
    Spider getOwner();

    default void setNumber(byte num) {
    }

    byte getNumber();

    default boolean canBeSeen() {
        return false;
    }

    default boolean isUncovered() {
        return false;
    }

    default boolean isOut() {
        return false;
    }
}
