package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record UncoverCardNotify(int id, byte num, boolean last) implements Packet<ClientPlayPacketListener> {
    public UncoverCardNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readByte(), buf.readBoolean());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.id);
        buf.writeByte(this.num);
        buf.writeBoolean(this.last);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleUncoverCard(this);
    }
}
