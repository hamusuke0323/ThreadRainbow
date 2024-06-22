package com.hamusuke.threadr.network.protocol.packet.serverbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.util.List;

public record RemoveTopicReq(List<Integer> topicIds) implements Packet<ServerCommonPacketListener> {
    public RemoveTopicReq(IntelligentByteBuf buf) {
        this(buf.<List<Integer>, Integer>readList(IntelligentByteBuf::readVariableInt, list -> list.stream().distinct().toList()));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.topicIds, (integer, intelligentByteBuf) -> intelligentByteBuf.writeVariableInt(integer));
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleRemoveTopic(this);
    }
}
