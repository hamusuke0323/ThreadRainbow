package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record StartTopicSelectionNotify(int firstTopicId) implements Packet<ClientPlayPacketListener> {
    public StartTopicSelectionNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.firstTopicId);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleStartTopicSelection(this);
    }
}
