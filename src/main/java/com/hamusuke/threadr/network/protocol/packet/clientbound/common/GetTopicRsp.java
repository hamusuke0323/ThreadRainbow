package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record GetTopicRsp(TopicEntry topicEntry) implements Packet<ClientCommonPacketListener> {
    public GetTopicRsp(IntelligentByteBuf buf) {
        this(TopicEntry.from(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.topicEntry.writeTo(buf);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleGetTopic(this);
    }
}
