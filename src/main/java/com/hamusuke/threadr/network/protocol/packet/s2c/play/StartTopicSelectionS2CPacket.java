package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record StartTopicSelectionS2CPacket(Topic firstTopic) implements Packet<ClientPlayPacketListener> {
    public StartTopicSelectionS2CPacket(IntelligentByteBuf buf) {
        this(Topic.readFrom(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.firstTopic.writeTo(buf);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleStartTopicSelection(this);
    }
}
