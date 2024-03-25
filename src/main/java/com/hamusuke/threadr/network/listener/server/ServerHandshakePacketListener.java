package com.hamusuke.threadr.network.listener.server;

import com.hamusuke.threadr.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends ServerPacketListener {
    void onHandshake(HandshakeC2SPacket packet);
}
