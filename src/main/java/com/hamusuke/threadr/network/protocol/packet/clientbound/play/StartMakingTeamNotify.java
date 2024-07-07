package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.game.team.TeamEntry.TeamSerializer;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.util.List;

public record StartMakingTeamNotify(List<TeamSerializer> serializers) implements Packet<ClientPlayPacketListener> {
    public StartMakingTeamNotify(IntelligentByteBuf buf) {
        this(buf.<List<TeamSerializer>, TeamSerializer>readList(TeamSerializer::from, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.serializers, TeamSerializer::writeTo);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleStartMakingTeam(this);
    }
}
