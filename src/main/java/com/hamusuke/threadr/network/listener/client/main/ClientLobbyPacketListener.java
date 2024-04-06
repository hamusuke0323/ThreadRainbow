package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.RoomListNotify;

public interface ClientLobbyPacketListener extends ClientCommonPacketListener {
    void handleRoomList(RoomListNotify packet);
}
