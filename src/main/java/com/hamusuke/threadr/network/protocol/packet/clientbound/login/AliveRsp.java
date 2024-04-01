package com.hamusuke.threadr.network.protocol.packet.clientbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record AliveRsp() implements Packet<ClientLoginPacketListener> {
    public AliveRsp(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
    }
}
