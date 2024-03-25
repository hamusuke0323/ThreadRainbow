package com.hamusuke.threadr.server.network.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.lobby.StartGameC2SPacket;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;

public class ServerLobbyPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerLobbyPacketListener, ServerCommonPacketListener {
    public ServerLobbyPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
    }

    @Override
    public void handleStartGame(StartGameC2SPacket packet) {
        if (this.server.isHost(this.spider)) {
            this.server.startGame();
        }
    }
}
