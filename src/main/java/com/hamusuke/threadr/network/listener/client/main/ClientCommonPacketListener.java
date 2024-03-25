package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.*;

public interface ClientCommonPacketListener extends PacketListener {
    void handleChatPacket(ChatS2CPacket packet);

    void handlePongPacket(PongS2CPacket packet);

    void handleDisconnectPacket(DisconnectS2CPacket packet);

    void handleJoinPacket(JoinSpiderS2CPacket packet);

    void handleRTTPacket(RTTS2CPacket packet);

    void handleLeavePacket(LeaveSpiderS2CPacket packet);

    void handleChangeHost(ChangeHostS2CPacket packet);
}
