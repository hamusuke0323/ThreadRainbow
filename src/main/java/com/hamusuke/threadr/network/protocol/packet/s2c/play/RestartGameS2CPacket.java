package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record RestartGameS2CPacket() implements Packet<ClientPlayPacketListener> {
    public RestartGameS2CPacket(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRestart(this);
    }
}
