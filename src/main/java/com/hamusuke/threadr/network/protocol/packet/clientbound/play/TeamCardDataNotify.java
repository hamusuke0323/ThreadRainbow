package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.util.List;

public record TeamCardDataNotify(TeamType teamType, List<Integer> cards) implements Packet<ClientPlayPacketListener> {
    public TeamCardDataNotify(IntelligentByteBuf buf) {
        this(buf.readEnum(TeamType.class), buf.readList(IntelligentByteBuf::readVariableInt, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.teamType);
        buf.writeList(this.cards, (i, b) -> b.writeVariableInt(i));
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTeamCardData(this);
    }
}
