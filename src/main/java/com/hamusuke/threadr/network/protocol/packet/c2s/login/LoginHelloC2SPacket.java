package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class LoginHelloC2SPacket implements Packet<ServerLoginPacketListener> {
    public LoginHelloC2SPacket() {
    }

    public LoginHelloC2SPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.onHello(this);
    }
}
