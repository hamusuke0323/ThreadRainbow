package com.hamusuke.threadr.network.listener.server.handshake;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.handshake.HandshakeReq;

public interface ServerHandshakePacketListener extends ServerPacketListener {
    void handleHandshake(HandshakeReq packet);
}
