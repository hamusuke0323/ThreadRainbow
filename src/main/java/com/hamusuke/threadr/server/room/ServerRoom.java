package com.hamusuke.threadr.server.room;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.game.mode.SpidersThreadV2Game;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.SpiderJoinNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.SpiderLeaveNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.JoinRoomSuccNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.RestartGameNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.StartGameNotify;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.room.RoomInfo;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.ServerPlayPacketListenerImpl;
import com.hamusuke.threadr.server.network.listener.main.ServerRoomPacketListenerImpl;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ServerRoom extends Room {
    private final ThreadRainbowServer server;
    private final List<ServerSpider> spiders = Collections.synchronizedList(Lists.newArrayList());
    private final List<ServerSpider> spiderList;
    private final String password;
    @Nullable
    private ServerSpider host;
    private SpidersThreadV2Game game;

    public ServerRoom(ThreadRainbowServer server, String roomName, String password) {
        super(roomName);
        this.server = server;
        this.password = password;
        this.spiderList = Collections.unmodifiableList(this.spiders);
    }

    public RoomInfo toInfo() {
        return new RoomInfo(this.id, this.roomName, this.host == null ? "" : this.host.getDisplayName(), this.getSpiders().size(), this.hasPassword());
    }

    @Override
    public synchronized void join(Spider spider) {
        var serverSpider = (ServerSpider) spider;
        serverSpider.currentRoom = this;
        serverSpider.sendPacket(new JoinRoomSuccNotify(this.toInfo()));
        new ServerRoomPacketListenerImpl(this.server, serverSpider.connection.getConnection(), serverSpider);

        this.sendPacketToAllInRoom(new SpiderJoinNotify(serverSpider));
        this.spiders.forEach(s -> serverSpider.sendPacket(new SpiderJoinNotify(s)));
        this.spiders.add(serverSpider);

        if (this.host == null) {
            this.host = (ServerSpider) spider;
        }

        this.sendPacketToAllInRoom(new ChangeHostNotify(this.host));
        this.sendPacketToAllInRoom(new ChatNotify("%s[%s] が部屋に参加しました".formatted(serverSpider.getDisplayName(), serverSpider.connection.getConnection().getAddress())));
    }

    @Override
    public synchronized void leave(Spider spider) {
        var serverSpider = (ServerSpider) spider;
        serverSpider.currentRoom = null;
        if (this.game != null) {
            this.game.onSpiderLeft(serverSpider);
        }
        this.spiders.remove(serverSpider);
        if (this.spiders.isEmpty()) {
            this.server.removeRoom(this);
            return;
        }

        this.sendPacketToAllInRoom(new SpiderLeaveNotify(serverSpider));

        if (this.host == serverSpider) {
            this.host = null;
        }

        if (!this.spiders.isEmpty()) {
            this.host = this.spiders.get(0);
        }

        if (this.host != null) {
            this.sendPacketToAllInRoom(new ChangeHostNotify(this.host));
        }

        this.sendPacketToAllInRoom(new ChatNotify("%s[%s] が部屋から退出しました".formatted(serverSpider.getDisplayName(), serverSpider.connection.getConnection().getAddress())));
    }

    @Override
    public List<ServerSpider> getSpiders() {
        return this.spiderList;
    }

    public void startGame() {
        var spiders = this.getSpiders();
        if (spiders.size() > 100) {
            this.sendPacketToAllInRoom(new ChatNotify("このゲームは100人まで遊べます"));
            return;
        }

        this.game = new SpidersThreadV2Game(this.server, this, spiders);
        this.game.getPlayingSpiders().forEach(spider -> {
            spider.sendPacket(new StartGameNotify());
            new ServerPlayPacketListenerImpl(this.server, spider.connection.getConnection(), spider);
        });
        this.game.start();
    }

    public void restartGame() {
        if (this.game == null) {
            return;
        }

        this.game = new SpidersThreadV2Game(this.server, this, this.game.getPlayingSpiders());
        this.game.sendPacketToAllInGame(new RestartGameNotify());
        this.game.start();
    }

    public SpidersThreadV2Game getGame() {
        return this.game;
    }

    public void sendPacketToAllInRoom(Packet<?> packet) {
        this.spiders.forEach(s -> s.sendPacket(packet));
    }

    public void sendPacketToOthersInRoom(ServerSpider sender, Packet<?> packet) {
        this.spiders.stream()
                .filter(spider -> spider != sender)
                .forEach(spider -> spider.sendPacket(packet));
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

    public void changeHost(ServerSpider serverSpider) {
        this.host = serverSpider;
        this.sendPacketToAllInRoom(new ChangeHostNotify(this.host));
    }

    public boolean changeHost(String name) {
        var host = this.spiders.stream().filter(s -> s.getName().equals(name)).findFirst();
        host.ifPresent(this::changeHost);
        return host.isPresent();
    }

    public boolean doesSpiderExist(String name) {
        return this.spiders.stream().anyMatch(s -> s.getName().equals(name));
    }

    public boolean shouldNotBeHost(String name) {
        var host = this.spiders.stream().filter(s -> s.getName().equals(name)).findFirst();
        return host.isPresent() && this.game != null && !this.game.getPlayingSpiders().contains(host.get());
    }

    public boolean isHost(String name) {
        return this.host != null && this.host.getName().equals(name);
    }
}
