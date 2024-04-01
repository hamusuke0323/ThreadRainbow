package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record PongRsp(long clientTime) implements Packet<ClientCommonPacketListener> {
    public PongRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePongPacket(this);
    }
}
