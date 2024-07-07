package com.hamusuke.threadr.game.mode;

import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.util.List;

public interface Game {
    void tick();

    void start();

    void restart();

    boolean setTopic(int topicId);

    List<ServerSpider> getPlayingSpiders();

    void onSpiderLeft(ServerSpider spider);

    void sendPacketToAllInGame(Packet<?> packet);
}
