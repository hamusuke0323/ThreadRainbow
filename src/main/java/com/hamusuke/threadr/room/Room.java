package com.hamusuke.threadr.room;

import com.hamusuke.threadr.network.Spider;

import java.util.List;

public abstract class Room {
    protected final String roomName;
    protected final JoinLimitation limitation;

    protected Room(String roomName, JoinLimitation limitation) {
        this.roomName = roomName;
        this.limitation = limitation;
    }

    public void tick() {
    }

    public abstract void join(Spider spider);

    public abstract void leave(Spider spider);

    public abstract List<Spider> getSpiders();

    public enum JoinLimitation {
        ANYONE,
        INVITED
    }
}
