package com.hamusuke.threadr.network.protocol.packet.clientbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record EnterNameReq(String msg) implements Packet<ClientLoginPacketListener> {
    public EnterNameReq() {
        this("");
    }

    public EnterNameReq(IntelligentByteBuf byteBuf) {
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
