package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.StartGameReq;

public interface ServerLobbyPacketListener extends ServerCommonPacketListener {
    void handleStartGame(StartGameReq packet);
}
