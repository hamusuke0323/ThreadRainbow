package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
    public LoginDisconnectS2CPacket() {
    }

    public LoginDisconnectS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onDisconnect(this);
    }
}
