package com.hamusuke.threadr.network.protocol.packet.clientbound.info;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record InfoHandshakeDoneNotify() implements Packet<ClientInfoPacketListener> {
    public InfoHandshakeDoneNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientInfoPacketListener listener) {
        listener.handleHandshakeDone(this);
    }
}
