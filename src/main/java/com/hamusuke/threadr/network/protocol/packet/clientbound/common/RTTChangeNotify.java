package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record RTTChangeNotify(int spiderId, int rtt) implements Packet<ClientCommonPacketListener> {
    public RTTChangeNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVariableInt(), byteBuf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.spiderId);
        byteBuf.writeVariableInt(this.rtt);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRTTPacket(this);
    }
}
