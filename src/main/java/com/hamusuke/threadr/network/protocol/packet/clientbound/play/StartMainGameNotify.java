package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.util.List;

public record StartMainGameNotify(List<Integer> cards) implements Packet<ClientPlayPacketListener> {
    public StartMainGameNotify(IntelligentByteBuf buf) {
        this(buf.<List<Integer>, Integer>readList(IntelligentByteBuf::readVariableInt, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.cards, (integer, buf1) -> buf1.writeVariableInt(integer));
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleStartMainGame(this);
    }
}
