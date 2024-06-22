package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.util.List;

public record RemoveTopicNotify(List<Integer> removedTopicIds) implements Packet<ClientCommonPacketListener> {
    public RemoveTopicNotify(IntelligentByteBuf buf) {
        this(buf.<List<Integer>, Integer>readList(IntelligentByteBuf::readVariableInt, list -> list.stream().distinct().toList()));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.removedTopicIds, (integer, intelligentByteBuf) -> intelligentByteBuf.writeVariableInt(integer));
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRemoveTopic(this);
    }
}
