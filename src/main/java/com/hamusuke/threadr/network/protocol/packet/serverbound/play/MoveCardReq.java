package com.hamusuke.threadr.network.protocol.packet.serverbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record MoveCardReq(int from, int to) implements Packet<ServerPlayPacketListener> {
    public MoveCardReq(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.from);
        buf.writeVariableInt(this.to);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleMoveCard(this);
    }
}
