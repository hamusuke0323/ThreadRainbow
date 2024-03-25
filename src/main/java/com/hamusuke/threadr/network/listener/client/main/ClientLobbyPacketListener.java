package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.s2c.lobby.StartGameS2CPacket;

public interface ClientLobbyPacketListener extends ClientCommonPacketListener {
    void handleStartGame(StartGameS2CPacket packet);
}
