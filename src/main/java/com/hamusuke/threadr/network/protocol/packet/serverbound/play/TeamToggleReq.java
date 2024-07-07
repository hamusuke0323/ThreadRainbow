package com.hamusuke.threadr.network.protocol.packet.serverbound.play;

import com.hamusuke.threadr.game.team.TeamEntry.TeamSerializer;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TeamToggleReq(TeamSerializer teamSerializer) implements Packet<ServerPlayPacketListener> {
    public TeamToggleReq(IntelligentByteBuf buf) {
        this(TeamSerializer.from(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.teamSerializer.writeTo(buf);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleTeamToggle(this);
    }
}
