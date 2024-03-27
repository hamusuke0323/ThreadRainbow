package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.io.IOException;

public record SpiderExitGameS2CPacket(int id) implements Packet<ClientPlayPacketListener> {
    public SpiderExitGameS2CPacket(ServerSpider spider) {
        this(spider.getId());
    }

    public SpiderExitGameS2CPacket(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleSpiderExit(this);
    }
}
