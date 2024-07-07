package com.hamusuke.threadr.server.room;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.game.card.GameDeck;
import com.hamusuke.threadr.game.mode.Game;
import com.hamusuke.threadr.game.mode.GameMode;
import com.hamusuke.threadr.game.mode.SpidersThreadV2Game;
import com.hamusuke.threadr.game.mode.ThreadRainbowGame;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.*;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.JoinRoomSuccNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.ExitGameNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.StartGameNotify;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.room.RoomInfo;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.game.topic.ServerTopicList;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.ServerRoomPacketListenerImpl;
import com.hamusuke.threadr.server.network.listener.main.play.ServerPlayPacketListenerImpl;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServerRoom extends Room {
    private final ThreadRainbowServer server;
    private final List<ServerSpider> spiders = Collections.synchronizedList(Lists.newArrayList());
    private final List<ServerSpider> spiderList;
    private final String password;
    @Nullable
    private ServerSpider host;
    private final GameDeck deck = new GameDeck();
    public GameMode curGameMode = GameMode.SPIDERS_THREAD_V2;
    @Nullable
    private Game game;

    public ServerRoom(ThreadRainbowServer server, String roomName, String password) {
        super(roomName, new ServerTopicList(server.getTopicLoader().getTopics()));
        this.server = server;
        this.password = password;
        this.spiderList = Collections.unmodifiableList(this.spiders);
    }

    @Override
    public void tick() {
        if (this.game != null) {
            this.game.tick();
        }

        super.tick();
    }

    public RoomInfo toInfo() {
        return new RoomInfo(this.id, this.roomName, this.host == null ? "" : this.host.getDisplayName(), this.getSpiders().size(), this.hasPassword());
    }

    @Override
    public synchronized void join(Spider spider) {
        var serverSpider = (ServerSpider) spider;
        serverSpider.curRoom = this;
        serverSpider.sendPacket(new JoinRoomSuccNotify(this.toInfo()));
        new ServerRoomPacketListenerImpl(this.server, serverSpider.connection.getConnection(), serverSpider);

        this.sendPacketToAllInRoom(new SpiderJoinNotify(serverSpider));
        this.spiders.forEach(s -> serverSpider.sendPacket(new SpiderJoinNotify(s)));
        this.spiders.add(serverSpider);

        if (this.host == null) {
            this.host = (ServerSpider) spider;
        }

        this.sendPacketToAllInRoom(new ChangeHostNotify(this.host));
        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋に参加しました".formatted(serverSpider.getDisplayName())));
    }

    @Override
    public synchronized void leave(Spider spider) {
        var serverSpider = (ServerSpider) spider;
        serverSpider.curRoom = null;
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

        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋から退出しました".formatted(serverSpider.getDisplayName())));
    }

    @Override
    public List<ServerSpider> getSpiders() {
        return this.spiderList;
    }

    public synchronized TopicEntry addCustomTopic(Topic topic) {
        var e = this.getTopicList().addTopic(topic);
        this.sendPacketToAllInRoom(new NewTopicAddNotify(e));
        return e;
    }

    @Nullable
    public synchronized Topic removeTopic(int topicId) {
        var removed = this.getTopicList().removeTopicEntry(topicId)
                .map(TopicEntry::topic)
                .orElse(null);

        if (removed != null) {
            this.sendPacketToAllInRoom(new RemoveTopicNotify(Collections.singletonList(topicId)));
        }

        return removed;
    }

    public synchronized void removeTopics(List<Integer> topicIds) {
        topicIds = topicIds.stream().distinct().toList();
        topicIds.forEach(this.getTopicList()::removeTopicEntry);
        this.sendPacketToAllInRoom(new RemoveTopicNotify(topicIds));
    }

    public Map<Integer, TopicEntry> getTopics() {
        return this.getTopicList().getTopics();
    }

    @Override
    public ServerTopicList getTopicList() {
        return (ServerTopicList) this.topicList;
    }

    private Game newGameByGameMode(List<ServerSpider> spiders) {
        return switch (this.curGameMode) {
            case SPIDERS_THREAD_V2 -> new SpidersThreadV2Game(this.server, this, spiders);
            case THREAD_RAINBOW -> new ThreadRainbowGame(this.server, this, spiders);
        };
    }

    public void startGame() {
        var spiders = this.getSpiders();
        var mode = this.curGameMode;
        if (spiders.size() > mode.getMaxSpiders()) {
            this.sendPacketToAllInRoom(new ChatNotify("このゲームモードでは%d人まで遊べます".formatted(mode.getMaxSpiders())));
            return;
        }

        if (spiders.size() < mode.getMinSpiders()) {
            this.sendPacketToAllInRoom(new ChatNotify("このゲームモードで遊ぶためには%d人以上必要です".formatted(mode.getMinSpiders())));
            return;
        }

        this.deck.returnAllCards();
        this.game = this.newGameByGameMode(spiders);
        this.game.getPlayingSpiders().forEach(spider -> {
            spider.sendPacket(new StartGameNotify(mode));
            ServerPlayPacketListenerImpl.newListenerByGameMode(mode, this.server, spider.connection.getConnection(), spider);
        });
        this.game.start();
    }

    public synchronized void endGame() {
        if (this.game == null) {
            return;
        }

        this.game.getPlayingSpiders().forEach(serverSpider -> {
            serverSpider.sendPacket(new ExitGameNotify());
            new ServerRoomPacketListenerImpl(this.server, serverSpider.connection.getConnection(), serverSpider);
        });
        this.game = null;
    }

    @Nullable
    public Game getGame() {
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

    public GameDeck getDeck() {
        return this.deck;
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

    @Nullable
    @Override
    public ServerSpider getSpider(int id) {
        return (ServerSpider) super.getSpider(id);
    }
}
