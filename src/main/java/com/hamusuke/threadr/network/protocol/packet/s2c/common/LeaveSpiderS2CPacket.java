package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LeaveSpiderS2CPacket(int id) implements Packet<ClientCommonPacketListener> {
    public LeaveSpiderS2CPacket(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeavePacket(this);
    }
}
