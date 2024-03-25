package com.hamusuke.threadr.network.protocol.packet.s2c.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

public class JoinSpiderS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;
    private final String name;

    public JoinSpiderS2CPacket(ServerSpider serverSpider) {
        this.id = serverSpider.getId();
        this.name = serverSpider.getName();
    }

    public JoinSpiderS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVariableInt();
        this.name = byteBuf.readString();
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

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
