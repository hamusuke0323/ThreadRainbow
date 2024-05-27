package com.hamusuke.threadr.network.listener.client.info;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.info.InfoHandshakeDoneNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.info.ServerInfoRsp;

public interface ClientInfoPacketListener extends PacketListener {
    void handleInfoRsp(ServerInfoRsp packet);

    void handleHandshakeDone(InfoHandshakeDoneNotify packet);
}
