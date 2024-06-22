package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record NewTopicAddNotify(TopicEntry newTopic) implements Packet<ClientCommonPacketListener> {
    public NewTopicAddNotify(IntelligentByteBuf buf) {
        this(TopicEntry.from(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.newTopic.writeTo(buf);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleNewTopicAdd(this);
    }
}
