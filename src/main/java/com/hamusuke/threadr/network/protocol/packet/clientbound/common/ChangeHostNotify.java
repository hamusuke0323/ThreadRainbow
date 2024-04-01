package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

public record ChangeHostNotify(int id) implements Packet<ClientCommonPacketListener> {
    public ChangeHostNotify(ServerSpider spider) {
        this(spider.getId());
    }

    public ChangeHostNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChangeHost(this);
    }
}
