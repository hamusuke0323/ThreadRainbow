package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

public record RemoteCardGivenS2CPacket(int id) implements Packet<ClientPlayPacketListener> {
    public RemoteCardGivenS2CPacket(ServerSpider others) {
        this(others.getId());
    }

    public RemoteCardGivenS2CPacket(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemoteCard(this);
    }
}
