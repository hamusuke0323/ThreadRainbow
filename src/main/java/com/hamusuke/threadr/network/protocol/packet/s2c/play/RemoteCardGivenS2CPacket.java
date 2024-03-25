package com.hamusuke.threadr.network.protocol.packet.s2c.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.io.IOException;

public class RemoteCardGivenS2CPacket implements Packet<ClientPlayPacketListener> {
    private final int id;

    public RemoteCardGivenS2CPacket(ServerSpider others) {
        this.id = others.getId();
    }

    public RemoteCardGivenS2CPacket(IntelligentByteBuf buf) {
        this.id = buf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
        buf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemoteCard(this);
    }

    public int getId() {
        return this.id;
    }
}
