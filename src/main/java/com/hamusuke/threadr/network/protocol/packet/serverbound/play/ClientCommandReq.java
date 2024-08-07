package com.hamusuke.threadr.network.protocol.packet.serverbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ClientCommandReq(Command command) implements Packet<ServerPlayPacketListener> {
    public ClientCommandReq(IntelligentByteBuf buf) {
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
        FINISH_MAKING_TEAM,
        START_TOPIC_SELECTION,
        CHANGE_TOPIC,
        DECIDE_TOPIC,
        START_MAIN_GAME,
        FINISH,
        TEAM_FINISH(false),
        UNCOVER,
        NEXT,
        RESTART,
        RESTART_WITH_THE_SAME_TEAM,
        EXIT(false);

        private final boolean hostOnly;

        Command(boolean hostOnly) {
            this.hostOnly = hostOnly;
        }

        Command() {
            this(true);
        }

        public boolean isHostOnly() {
            return this.hostOnly;
        }
    }
}
