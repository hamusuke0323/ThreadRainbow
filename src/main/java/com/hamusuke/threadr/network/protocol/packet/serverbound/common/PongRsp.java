package com.hamusuke.threadr.network.protocol.packet.serverbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record PongRsp(long serverTime) implements Packet<ServerCommonPacketListener> {
    public PongRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.serverTime);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handlePongPacket(this);
    }
}
