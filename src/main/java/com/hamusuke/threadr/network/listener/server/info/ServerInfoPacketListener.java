package com.hamusuke.threadr.network.listener.server.info;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.info.ServerInfoReq;

public interface ServerInfoPacketListener extends ServerPacketListener {
    void handleInfoReq(ServerInfoReq packet);
}
