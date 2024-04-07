package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.*;

public interface ServerCommonPacketListener extends ServerPacketListener {
    void handleDisconnect(DisconnectReq packet);

    void handleChatPacket(ChatReq packet);

    void handlePingPacket(PingReq packet);

    void handleRTTPacket(RTTChangeReq packet);

    void handleLeaveRoom(LeaveRoomReq packet);
}
