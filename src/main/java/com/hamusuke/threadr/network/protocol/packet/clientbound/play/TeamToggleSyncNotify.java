package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.game.team.TeamEntry.TeamSerializer;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TeamToggleSyncNotify(TeamSerializer teamSerializer) implements Packet<ClientPlayPacketListener> {
    public TeamToggleSyncNotify(IntelligentByteBuf buf) {
        this(TeamSerializer.from(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.teamSerializer.writeTo(buf);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTeamToggleSync(this);
    }
}
