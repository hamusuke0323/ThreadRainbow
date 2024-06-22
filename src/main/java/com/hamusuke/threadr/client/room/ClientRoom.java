package com.hamusuke.threadr.client.room;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.game.topic.ClientTopicList;
import com.hamusuke.threadr.client.network.spider.AbstractClientSpider;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.room.RoomInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ClientRoom extends Room {
    private final List<AbstractClientSpider> clientSpiders = Collections.synchronizedList(Lists.newArrayList());
    private final List<AbstractClientSpider> clientSpiderList;
    @Nullable
    private AbstractClientSpider host;

    public ClientRoom(ThreadRainbowClient client, String roomName) {
        super(roomName, new ClientTopicList(client));
        this.clientSpiderList = Collections.unmodifiableList(this.clientSpiders);
    }

    public static ClientRoom fromRoomInfo(ThreadRainbowClient client, RoomInfo info) {
        return new ClientRoom(client, info.roomName());
    }

    @Override
    public synchronized void join(Spider spider) {
        this.clientSpiders.add((AbstractClientSpider) spider);
    }

    @Override
    public synchronized void leave(Spider spider) {
        this.clientSpiders.remove((AbstractClientSpider) spider);
    }

    public synchronized void leave(int id) {
        this.clientSpiders.removeIf(p -> p.getId() == id);
    }

    @Override
    public ClientTopicList getTopicList() {
        return (ClientTopicList) this.topicList;
    }

    @Override
    public List<AbstractClientSpider> getSpiders() {
        return this.clientSpiderList;
    }

    @Nullable
    public AbstractClientSpider getHost() {
        return this.host;
    }

    public void setHost(@Nullable AbstractClientSpider abstractClientSpider) {
        this.host = abstractClientSpider;
    }

    public int getHostId() {
        return this.host != null ? this.host.getId() : -1;
    }
}
