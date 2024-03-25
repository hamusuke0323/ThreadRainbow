package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class RTTS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int spiderId;
    private final int rtt;

    public RTTS2CPacket(int spiderId, int rtt) {
        this.spiderId = spiderId;
        this.rtt = rtt;
    }

    public RTTS2CPacket(IntelligentByteBuf byteBuf) {
        this.spiderId = byteBuf.readVariableInt();
        this.rtt = byteBuf.readVariableInt();
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

    public int getSpiderId() {
        return this.spiderId;
    }

    public int getRtt() {
        return this.rtt;
    }
}
