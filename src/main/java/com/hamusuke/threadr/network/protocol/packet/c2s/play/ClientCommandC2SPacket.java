package com.hamusuke.threadr.network.protocol.packet.c2s.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ClientCommandC2SPacket(Command command) implements Packet<ServerPlayPacketListener> {
    public ClientCommandC2SPacket(IntelligentByteBuf buf) {
        this(buf.readEnum(Command.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.command);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleClientCommand(this);
    }

    public enum Command {
        START_SELECTING_TOPIC,
        RESELECT_TOPIC,
        DECIDE_TOPIC,
        FINISH,
        UNCOVER,
        RESTART,
        EXIT
    }
}
