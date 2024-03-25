package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class LeaveSpiderS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;

    public LeaveSpiderS2CPacket(int id) {
        this.id = id;
    }

    public LeaveSpiderS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeavePacket(this);
    }

    public int getId() {
        return this.id;
    }
}
