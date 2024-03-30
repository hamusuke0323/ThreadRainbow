package com.hamusuke.threadr.network.listener.server.info;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.info.ServerInfoRequestC2SPacket;

public interface ServerInfoPacketListener extends ServerPacketListener {
    void handleInfoReq(ServerInfoRequestC2SPacket packet);
}
