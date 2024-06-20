package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.util.List;

public record GetAllTopicsRsp(List<TopicEntry> topicEntries) implements Packet<ClientCommonPacketListener> {
    public GetAllTopicsRsp(IntelligentByteBuf buf) {
        this(buf.<List<TopicEntry>, TopicEntry>readList(TopicEntry::from, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.topicEntries, TopicEntry::writeTo);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleGetAllTopics(this);
    }
}
