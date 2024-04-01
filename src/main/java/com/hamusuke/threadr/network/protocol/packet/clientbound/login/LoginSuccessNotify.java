package com.hamusuke.threadr.network.protocol.packet.clientbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

public record LoginSuccessNotify(int id, String name) implements Packet<ClientLoginPacketListener> {
    public LoginSuccessNotify(ServerSpider spider) {
        this(spider.getId(), spider.getName());
    }

    public LoginSuccessNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVariableInt(), byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleSuccess(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.LOBBY;
    }
}
