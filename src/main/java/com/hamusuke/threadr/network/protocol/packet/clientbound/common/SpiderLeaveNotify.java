package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record SpiderLeaveNotify(int id) implements Packet<ClientCommonPacketListener> {
    public SpiderLeaveNotify(IntelligentByteBuf byteBuf) {
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
