package com.hamusuke.threadr.network.protocol.packet.serverbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record EnterNameRsp(String name) implements Packet<ServerLoginPacketListener> {
    public static final int MAX_NAME_LENGTH = 16;

    public EnterNameRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString(MAX_NAME_LENGTH));
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.name, MAX_NAME_LENGTH);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleEnterName(this);
    }
}
