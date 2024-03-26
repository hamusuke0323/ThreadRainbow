package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record CardMovedS2CPacket(int from, int to) implements Packet<ClientPlayPacketListener> {
    public CardMovedS2CPacket(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeVariableInt(this.from);
        buf.writeVariableInt(this.to);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleCardMoved(this);
    }
}
