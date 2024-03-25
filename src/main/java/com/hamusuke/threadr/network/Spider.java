package com.hamusuke.threadr.network;

import com.hamusuke.threadr.game.NumberCard;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Spider {
    private static final AtomicInteger SPIDER_ID_INCREMENTER = new AtomicInteger();
    protected int id = SPIDER_ID_INCREMENTER.getAndIncrement();
    private int ping;
    protected final String name;
    @Nullable
    protected NumberCard holdingCard;

    protected Spider(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Spider spider = (Spider) o;
        return this.id == spider.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
