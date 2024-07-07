package com.hamusuke.threadr.game.mode;

public enum GameMode {
    SPIDERS_THREAD_V2("スパイダーズスレッド2.0", 1, 100),
    THREAD_RAINBOW("レインボーノスレッド", 4, 14);

    private final String name;
    private final int minSpiders;
    private final int maxSpiders;

    GameMode(String name, int minSpiders, int maxSpiders) {
        this.name = name;
        this.minSpiders = minSpiders;
        this.maxSpiders = maxSpiders;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public int getMinSpiders() {
        return this.minSpiders;
    }

    public int getMaxSpiders() {
        return this.maxSpiders;
    }
}
