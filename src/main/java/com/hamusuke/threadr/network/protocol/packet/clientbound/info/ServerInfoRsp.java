package com.hamusuke.threadr.network.protocol.packet.clientbound.info;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ServerInfoRsp(int protocolVersion,
                            long clientTimeEcho) implements Packet<ClientInfoPacketListener> {
    public ServerInfoRsp(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.protocolVersion);
        buf.writeLong(this.clientTimeEcho);
    }

    @Override
    public void handle(ClientInfoPacketListener listener) {
        listener.handleInfoRsp(this);
    }
}
