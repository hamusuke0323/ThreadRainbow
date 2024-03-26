package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record PongS2CPacket(long clientTime) implements Packet<ClientCommonPacketListener> {
    public PongS2CPacket(IntelligentByteBuf byteBuf) {
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
