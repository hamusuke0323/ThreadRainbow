package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record UncoverCardS2CPacket(int id, byte num, boolean last) implements Packet<ClientPlayPacketListener> {
    public UncoverCardS2CPacket(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readByte(), buf.readBoolean());
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeVariableInt(this.id);
        buf.writeByte(this.num);
        buf.writeBoolean(this.last);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleUncoverCard(this);
    }
}
