package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TeamFirstFinishGameNotify() implements Packet<ClientPlayPacketListener> {
    public TeamFirstFinishGameNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTeamFirstFinishGame(this);
    }
}
