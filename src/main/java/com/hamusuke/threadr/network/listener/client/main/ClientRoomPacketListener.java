package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.clientbound.room.StartGameNotify;

public interface ClientRoomPacketListener extends ClientCommonPacketListener {
    void handleStartGame(StartGameNotify packet);
}
