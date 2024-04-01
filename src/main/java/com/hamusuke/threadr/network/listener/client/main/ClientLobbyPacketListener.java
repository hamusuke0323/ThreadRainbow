package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.StartGameNotify;

public interface ClientLobbyPacketListener extends ClientCommonPacketListener {
    void handleStartGame(StartGameNotify packet);
}
