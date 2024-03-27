package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record ExitGameSuccS2CPacket() implements Packet<ClientPlayPacketListener> {
    public ExitGameSuccS2CPacket(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleExit(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.LOBBY;
    }
}
