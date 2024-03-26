package com.hamusuke.threadr.network.protocol.packet.c2s.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record RTTC2SPacket(int rtt) implements Packet<ServerCommonPacketListener> {
    public RTTC2SPacket(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.rtt);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleRTTPacket(this);
    }
}
