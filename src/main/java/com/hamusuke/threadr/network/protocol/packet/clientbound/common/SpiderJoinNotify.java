package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

public record SpiderJoinNotify(int id, String name) implements Packet<ClientCommonPacketListener> {
    public SpiderJoinNotify(ServerSpider serverSpider) {
        this(serverSpider.getId(), serverSpider.getName());
    }

    public SpiderJoinNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVariableInt(), byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleJoinPacket(this);
    }
}
