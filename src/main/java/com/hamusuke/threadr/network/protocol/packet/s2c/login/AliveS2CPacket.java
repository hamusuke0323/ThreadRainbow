package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public class AliveS2CPacket implements Packet<ClientLoginPacketListener> {
    public AliveS2CPacket() {
    }

    public AliveS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
    }
}
