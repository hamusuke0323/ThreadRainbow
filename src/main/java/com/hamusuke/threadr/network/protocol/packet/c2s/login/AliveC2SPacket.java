package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record AliveC2SPacket() implements Packet<ServerLoginPacketListener> {
    public AliveC2SPacket(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handlePing(this);
    }
}
