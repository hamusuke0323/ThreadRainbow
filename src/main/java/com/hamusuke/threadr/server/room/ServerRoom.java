package com.hamusuke.threadr.server.room;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.room.RoomInfo;
import com.hamusuke.threadr.server.network.ServerSpider;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ServerRoom extends Room {
    private final List<ServerSpider> spiders = Collections.synchronizedList(Lists.newArrayList());
    private final String password;
    @Nullable
    private ServerSpider host;

    public ServerRoom(String roomName, String password) {
        super(roomName);
        this.password = password;
    }

    public RoomInfo toInfo() {
        return new RoomInfo(this.id, this.roomName, this.host.getDisplayName(), this.getSpiders().size(), this.hasPassword());
    }

    @Override
    public synchronized void join(Spider spider) {
        this.spiders.add((ServerSpider) spider);

        if (this.host == null) {
            this.host = (ServerSpider) spider;
        }
    }

    @Override
    public synchronized void leave(Spider spider) {
        this.spiders.remove((ServerSpider) spider);
    }

    @Override
    public List<Spider> getSpiders() {
        return ImmutableList.copyOf(this.spiders);
    }

    public boolean hasPassword() {
        return !this.password.isEmpty();
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isHost(ServerSpider spider) {
        return this.host == spider;
    }

    public boolean isHost(String name) {
        return this.host != null && this.host.getName().equals(name);
    }
}
