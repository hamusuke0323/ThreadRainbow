package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record AliveC2SPacket() implements Packet<ServerLoginPacketListener> {
    public AliveC2SPacket(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handlePing(this);
    }
}
