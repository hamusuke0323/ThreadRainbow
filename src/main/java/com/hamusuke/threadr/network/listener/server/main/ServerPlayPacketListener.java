package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket;

public interface ServerPlayPacketListener extends ServerCommonPacketListener {
    void handleClientCommand(ClientCommandC2SPacket packet);
}
