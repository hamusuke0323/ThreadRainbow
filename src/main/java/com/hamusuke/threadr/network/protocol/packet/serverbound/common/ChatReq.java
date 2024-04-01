package com.hamusuke.threadr.network.protocol.packet.serverbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ChatReq(String msg) implements Packet<ServerCommonPacketListener> {
    public ChatReq(IntelligentByteBuf byteBuf) {
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
