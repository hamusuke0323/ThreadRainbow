package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import javax.annotation.Nullable;

public record LoginSuccessS2CPacket(int id, String name) implements Packet<ClientLoginPacketListener> {
    public LoginSuccessS2CPacket(ServerSpider spider) {
        this(spider.getId(), spider.getName());
    }

    public LoginSuccessS2CPacket(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVariableInt(), byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onSuccess(this);
    }

    @Nullable
    @Override
    public Protocol nextProtocol() {
        return Protocol.LOBBY;
    }
}
