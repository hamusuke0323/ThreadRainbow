package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.ChatC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.DisconnectC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.PingC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.RTTC2SPacket;

public interface ServerCommonPacketListener extends ServerPacketListener {
    void handleDisconnect(DisconnectC2SPacket packet);

    void handleChatPacket(ChatC2SPacket packet);

    void handlePingPacket(PingC2SPacket packet);

    void handleRTTPacket(RTTC2SPacket packet);
}
