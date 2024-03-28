package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record SpiderLoginC2SPacket(String name) implements Packet<ServerLoginPacketListener> {
    public static final int MAX_NAME_LENGTH = 16;

    public SpiderLoginC2SPacket(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString(MAX_NAME_LENGTH));
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.name, MAX_NAME_LENGTH);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleLogin(this);
    }
}
