package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.c2s.lobby.StartGameC2SPacket;

public interface ServerLobbyPacketListener extends ServerCommonPacketListener {
    void handleStartGame(StartGameC2SPacket packet);
}
