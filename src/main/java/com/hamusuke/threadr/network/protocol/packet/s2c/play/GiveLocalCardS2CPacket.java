package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.io.IOException;

public class GiveLocalCardS2CPacket implements Packet<ClientPlayPacketListener> {
    private final int id;
    private final byte num;

    public GiveLocalCardS2CPacket(ServerSpider spider, byte num) {
        this.id = spider.getId();
        this.num = num;
    }

    public GiveLocalCardS2CPacket(IntelligentByteBuf buf) {
        this.id = buf.readVariableInt();
        this.num = buf.readByte();
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeVariableInt(this.id);
        buf.writeByte(this.num);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleGiveCard(this);
    }

    public int getId() {
        return this.id;
    }

    public byte getNum() {
        return this.num;
    }
}
