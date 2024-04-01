package com.hamusuke.threadr.network.protocol.packet.serverbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record AliveReq() implements Packet<ServerLoginPacketListener> {
    public AliveReq(IntelligentByteBuf byteBuf) {
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
