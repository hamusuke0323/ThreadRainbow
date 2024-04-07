package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.*;

public interface ClientCommonPacketListener extends PacketListener {
    void handleChatPacket(ChatNotify packet);

    void handlePongPacket(PongRsp packet);

    void handleDisconnectPacket(DisconnectNotify packet);

    void handleJoinPacket(SpiderJoinNotify packet);

    void handleRTTPacket(RTTChangeNotify packet);

    void handleLeavePacket(SpiderLeaveNotify packet);

    void handleChangeHost(ChangeHostNotify packet);

    void handleLeaveRoomSucc(LeaveRoomSuccNotify packet);
}
