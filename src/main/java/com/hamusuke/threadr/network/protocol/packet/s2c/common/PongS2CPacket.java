package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class PongS2CPacket implements Packet<ClientCommonPacketListener> {
    private final long clientTime;

    public PongS2CPacket(long clientTime) {
        this.clientTime = clientTime;
    }

    public PongS2CPacket(IntelligentByteBuf byteBuf) {
        this.clientTime = byteBuf.readLong();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePongPacket(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}
