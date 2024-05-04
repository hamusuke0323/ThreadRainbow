package com.hamusuke.threadr.network.listener.client.lobby;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.*;

public interface ClientLobbyPacketListener extends PacketListener {
    void handlePong(LobbyPongRsp packet);

    void handleDisconnectPacket(LobbyDisconnectNotify packet);

    void handleRoomList(RoomListNotify packet);

    void handleJoinRoomSucc(JoinRoomSuccNotify packet);

    void handleJoinRoomFail(JoinRoomFailNotify packet);

    void handleEnterPassword(EnterPasswordReq packet);
}
