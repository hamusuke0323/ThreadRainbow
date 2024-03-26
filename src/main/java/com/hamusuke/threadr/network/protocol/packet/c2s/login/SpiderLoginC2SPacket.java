package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record SpiderLoginC2SPacket(String name) implements Packet<ServerLoginPacketListener> {
    public SpiderLoginC2SPacket(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleLogin(this);
    }
}
