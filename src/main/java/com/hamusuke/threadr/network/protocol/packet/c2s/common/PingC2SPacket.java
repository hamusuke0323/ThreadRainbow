package com.hamusuke.threadr.network.protocol.packet.c2s.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class PingC2SPacket implements Packet<ServerCommonPacketListener> {
    private final long clientTime;

    public PingC2SPacket(long clientTime) {
        this.clientTime = clientTime;
    }

    public PingC2SPacket(IntelligentByteBuf byteBuf) {
        this.clientTime = byteBuf.readLong();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handlePingPacket(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}
