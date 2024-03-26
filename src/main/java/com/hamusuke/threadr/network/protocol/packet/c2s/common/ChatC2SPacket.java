package com.hamusuke.threadr.network.protocol.packet.c2s.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ChatC2SPacket(String msg) implements Packet<ServerCommonPacketListener> {
    public ChatC2SPacket(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleChatPacket(this);
    }
}
