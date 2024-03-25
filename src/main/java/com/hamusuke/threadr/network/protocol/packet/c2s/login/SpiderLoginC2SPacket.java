package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public class SpiderLoginC2SPacket implements Packet<ServerLoginPacketListener> {
    private final String name;

    public SpiderLoginC2SPacket(String name) {
        this.name = name;
    }

    public SpiderLoginC2SPacket(IntelligentByteBuf byteBuf) {
        this.name = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.onLogin(this);
    }

    public String getName() {
        return this.name;
    }
}
