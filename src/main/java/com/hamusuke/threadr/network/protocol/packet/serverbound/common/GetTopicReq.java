package com.hamusuke.threadr.network.protocol.packet.serverbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record GetTopicReq(int topicId) implements Packet<ServerCommonPacketListener> {
    public GetTopicReq(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.topicId);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleGetTopic(this);
    }
}
