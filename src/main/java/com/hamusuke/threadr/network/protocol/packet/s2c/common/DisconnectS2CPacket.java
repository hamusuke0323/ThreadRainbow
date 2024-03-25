package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class DisconnectS2CPacket implements Packet<ClientCommonPacketListener> {
    public DisconnectS2CPacket() {
    }

    public DisconnectS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }
}
