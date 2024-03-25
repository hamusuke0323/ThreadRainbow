package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class ChatS2CPacket implements Packet<ClientCommonPacketListener> {
    private final String msg;

    public ChatS2CPacket(String msg) {
        this.msg = msg;
    }

    public ChatS2CPacket(IntelligentByteBuf byteBuf) {
        this.msg = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChatPacket(this);
    }

    public String getMsg() {
        return this.msg;
    }
}
