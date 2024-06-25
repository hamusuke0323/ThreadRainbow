package com.hamusuke.threadr.network.protocol.packet.serverbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ChooseTopicReq(int topicId) implements Packet<ServerPlayPacketListener> {
    public ChooseTopicReq(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.topicId);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleChooseTopic(this);
    }
}
