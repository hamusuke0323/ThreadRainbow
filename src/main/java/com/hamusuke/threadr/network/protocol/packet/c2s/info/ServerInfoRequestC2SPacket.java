package com.hamusuke.threadr.network.protocol.packet.c2s.info;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ServerInfoRequestC2SPacket(long clientTime) implements Packet<ServerInfoPacketListener> {
    public ServerInfoRequestC2SPacket(IntelligentByteBuf buf) {
        this(buf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ServerInfoPacketListener listener) {
        listener.handleInfoReq(this);
    }
}
