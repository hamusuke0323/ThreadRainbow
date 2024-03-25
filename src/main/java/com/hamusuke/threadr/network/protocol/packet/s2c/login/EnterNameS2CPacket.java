package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public class EnterNameS2CPacket implements Packet<ClientLoginPacketListener> {
    private final String msg;

    public EnterNameS2CPacket() {
        this("");
    }

    public EnterNameS2CPacket(String msg) {
        this.msg = msg;
    }

    public EnterNameS2CPacket(IntelligentByteBuf byteBuf) {
        this.msg = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onEnterName(this);
    }

    public String getMsg() {
        return this.msg;
    }
}
