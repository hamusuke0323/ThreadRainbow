package com.hamusuke.threadr.network.protocol.packet.serverbound.common;

import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record CreateTopicReq(Topic topic) implements Packet<ServerCommonPacketListener> {
    public CreateTopicReq(IntelligentByteBuf buf) {
        this(Topic.readFrom(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.topic.writeTo(buf);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleCreateTopic(this);
    }
}
