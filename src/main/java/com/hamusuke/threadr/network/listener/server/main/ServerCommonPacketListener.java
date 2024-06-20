package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.*;

public interface ServerCommonPacketListener extends ServerPacketListener {
    void handleDisconnect(DisconnectReq packet);

    void handleChatPacket(ChatReq packet);

    void handlePongPacket(PongRsp packet);

    void handleLeaveRoom(LeaveRoomReq packet);

    void handleGetAllTopics(GetAllTopicsReq packet);
}
