package com.hamusuke.threadr.room;

import com.hamusuke.threadr.game.topic.TopicList;
import com.hamusuke.threadr.network.Spider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Room {
    public static final int MAX_ROOM_NAME_LENGTH = 64;
    public static final int MAX_ROOM_PASSWD_LENGTH = 16;
    private static final AtomicInteger ROOM_ID_INCREMENTER = new AtomicInteger();
    protected int id = ROOM_ID_INCREMENTER.getAndIncrement();
    protected final String roomName;
    protected final TopicList topicList;

    protected Room(String roomName, TopicList topicList) {
        this.roomName = roomName;
        this.topicList = topicList;
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

    public TopicList getTopicList() {
        return this.topicList;
    }

    @Nullable
    public Spider getSpider(int id) {
        return this.getSpiders().stream().filter(spider -> spider.getId() == id).findFirst().orElse(null);
    }
}
