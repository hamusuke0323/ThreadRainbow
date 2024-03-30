package com.hamusuke.threadr.network.listener.client.info;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.info.ServerInfoResponseS2CPacket;

public interface ClientInfoPacketListener extends PacketListener {
    void handleInfoRsp(ServerInfoResponseS2CPacket packet);
}
