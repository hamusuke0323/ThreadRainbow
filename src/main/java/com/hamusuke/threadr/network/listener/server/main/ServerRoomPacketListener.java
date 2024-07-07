package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.serverbound.room.SelectGameModeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.StartGameReq;

public interface ServerRoomPacketListener extends ServerCommonPacketListener {
    void handleStartGame(StartGameReq packet);

    void handleSelectGameRule(SelectGameModeReq packet);
}
