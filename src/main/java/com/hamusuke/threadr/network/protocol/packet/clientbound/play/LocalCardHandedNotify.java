package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LocalCardHandedNotify(byte num) implements Packet<ClientPlayPacketListener> {
    public LocalCardHandedNotify(IntelligentByteBuf buf) {
        this(buf.readByte());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeByte(this.num);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleGiveCard(this);
    }
}
