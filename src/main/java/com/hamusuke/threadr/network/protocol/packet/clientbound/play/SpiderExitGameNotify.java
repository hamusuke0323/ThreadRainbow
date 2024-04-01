package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

public record SpiderExitGameNotify(int id) implements Packet<ClientPlayPacketListener> {
    public SpiderExitGameNotify(ServerSpider spider) {
        this(spider.getId());
    }

    public SpiderExitGameNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleSpiderExit(this);
    }
}
