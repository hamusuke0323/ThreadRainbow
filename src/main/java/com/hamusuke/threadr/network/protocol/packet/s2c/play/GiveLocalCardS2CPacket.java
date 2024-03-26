package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record GiveLocalCardS2CPacket(byte num) implements Packet<ClientPlayPacketListener> {
    public GiveLocalCardS2CPacket(IntelligentByteBuf buf) {
        this(buf.readByte());
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeByte(this.num);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleGiveCard(this);
    }
}
