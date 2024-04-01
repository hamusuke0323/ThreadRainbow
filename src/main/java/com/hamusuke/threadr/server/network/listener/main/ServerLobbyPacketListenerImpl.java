package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.StartGameReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;

public class ServerLobbyPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerLobbyPacketListener {
    public ServerLobbyPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
    }

    @Override
    public void handleStartGame(StartGameReq packet) {
        if (!this.server.isHost(this.spider)) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        this.server.startGame();
    }
}
