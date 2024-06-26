package com.hamusuke.threadr.network.protocol.packet.serverbound.handshake;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record HandshakeReq(int protocolVersion,
                           Protocol intendedProtocol) implements Packet<ServerHandshakePacketListener> {
    public HandshakeReq(Protocol intendedProtocol) {
        this(Constants.PROTOCOL_VERSION, intendedProtocol);
    }

    public HandshakeReq(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), Protocol.byId(buf.readVariableInt()));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.protocolVersion);
        buf.writeVariableInt(this.intendedProtocol.getStateId());
    }

    @Override
    public void handle(ServerHandshakePacketListener listener) {
        listener.handleHandshake(this);
    }

    @Override
    public Protocol nextProtocol() {
        return this.intendedProtocol;
    }
}
