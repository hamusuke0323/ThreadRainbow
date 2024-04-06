package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.CreateRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.JoinRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListQueryReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListReq;

public interface ServerLobbyPacketListener extends ServerCommonPacketListener {
    void handleRoomList(RoomListReq packet);

    void handleRoomListQuery(RoomListQueryReq packet);

    void handleCreateRoom(CreateRoomReq packet);

    void handleJoinRoom(JoinRoomReq packet);
}
