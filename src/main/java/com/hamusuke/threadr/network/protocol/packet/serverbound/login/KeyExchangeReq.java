package com.hamusuke.threadr.network.protocol.packet.serverbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record KeyExchangeReq() implements Packet<ServerLoginPacketListener> {
    public KeyExchangeReq(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleKeyEx(this);
    }
}
