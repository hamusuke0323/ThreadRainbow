package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record CardMoveNotify(int from, int to) implements Packet<ClientPlayPacketListener> {
    public CardMoveNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.from);
        buf.writeVariableInt(this.to);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleCardMoved(this);
    }
}
