package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.*;

public interface ServerLobbyPacketListener extends ServerCommonPacketListener {
    void handleRoomList(RoomListReq packet);

    void handleRoomListQuery(RoomListQueryReq packet);

    void handleCreateRoom(CreateRoomReq packet);

    void handleJoinRoom(JoinRoomReq packet);

    void handleEnterPassword(EnterPasswordRsp packet);
}
