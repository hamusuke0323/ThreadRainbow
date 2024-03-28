package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record EnterNameS2CPacket(String msg) implements Packet<ClientLoginPacketListener> {
    public EnterNameS2CPacket() {
        this("");
    }

    public EnterNameS2CPacket(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleEnterName(this);
    }
}
