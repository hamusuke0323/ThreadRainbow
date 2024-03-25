package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.io.IOException;

public class ChangeHostS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;

    public ChangeHostS2CPacket(ServerSpider spider) {
        this.id = spider.getId();
    }

    public ChangeHostS2CPacket(IntelligentByteBuf buf) {
        this.id = buf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChangeHost(this);
    }

    public int getId() {
        return this.id;
    }
}
