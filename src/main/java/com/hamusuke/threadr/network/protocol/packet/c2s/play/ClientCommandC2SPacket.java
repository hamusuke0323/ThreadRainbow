package com.hamusuke.threadr.network.protocol.packet.c2s.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record ClientCommandC2SPacket(Command command) implements Packet<ServerPlayPacketListener> {
    public ClientCommandC2SPacket(IntelligentByteBuf buf) {
        this(buf.readEnum(Command.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeEnum(this.command);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleClientCommand(this);
    }

    public enum Command {
        START_SELECTING_TOPIC,
        SELECT_TOPIC_AGAIN,
        DECIDE_TOPIC
    }
}
