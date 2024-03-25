package com.hamusuke.threadr.game;

import java.util.Objects;

public class NumberCard {
    private final int num;

    public NumberCard(int num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberCard that = (NumberCard) o;
        return num == that.num;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num);
    }
}
