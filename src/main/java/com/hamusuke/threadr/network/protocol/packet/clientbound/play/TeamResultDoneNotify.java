package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TeamResultDoneNotify(TeamType teamType) implements Packet<ClientPlayPacketListener> {
    public TeamResultDoneNotify(IntelligentByteBuf buf) {
        this(buf.readEnum(TeamType.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.teamType);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTeamResultDone(this);
    }
}
