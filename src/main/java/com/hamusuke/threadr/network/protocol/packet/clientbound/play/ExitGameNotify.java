package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ExitGameNotify() implements Packet<ClientPlayPacketListener> {
    public ExitGameNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleExit(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.ROOM;
    }
}
