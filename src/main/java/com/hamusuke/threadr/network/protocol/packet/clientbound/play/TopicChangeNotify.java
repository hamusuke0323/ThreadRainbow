package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TopicChangeNotify(int topicId) implements Packet<ClientPlayPacketListener> {
    public TopicChangeNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.topicId);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleSelectTopic(this);
    }
}
