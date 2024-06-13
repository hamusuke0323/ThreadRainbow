package com.hamusuke.threadr.room;

import com.hamusuke.threadr.network.Spider;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Room {
    public static final int MAX_ROOM_NAME_LENGTH = 64;
    public static final int MAX_ROOM_PASSWD_LENGTH = 16;
    private static final AtomicInteger ROOM_ID_INCREMENTER = new AtomicInteger();
    protected int id = ROOM_ID_INCREMENTER.getAndIncrement();
    protected final String roomName;

    protected Room(String roomName) {
        this.roomName = roomName;
    }

    public void tick() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public abstract void join(Spider spider);

    public abstract void leave(Spider spider);

    public abstract List<? extends Spider> getSpiders();
}
