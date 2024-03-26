package com.hamusuke.threadr.network.protocol.packet.c2s.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record DisconnectC2SPacket() implements Packet<ServerCommonPacketListener> {
    public DisconnectC2SPacket(IntelligentByteBuf ignored) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleDisconnect(this);
    }
}
