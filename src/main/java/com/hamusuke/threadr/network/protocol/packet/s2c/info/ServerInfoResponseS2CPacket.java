package com.hamusuke.threadr.network.protocol.packet.s2c.info;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ServerInfoResponseS2CPacket(int protocolVersion,
                                          long clientTimeEcho) implements Packet<ClientInfoPacketListener> {
    public ServerInfoResponseS2CPacket(IntelligentByteBuf buf) {
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
