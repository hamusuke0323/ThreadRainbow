package com.hamusuke.threadr.network.protocol.packet.c2s.handshaking;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.ServerHandshakePacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import javax.annotation.Nullable;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
    private final int protocolVersion;
    private final Protocol intendedProtocol;

    public HandshakeC2SPacket(Protocol intendedProtocol) {
        this.protocolVersion = Constants.PROTOCOL_VERSION;
        this.intendedProtocol = intendedProtocol;
    }

    public HandshakeC2SPacket(IntelligentByteBuf buf) {
        this.protocolVersion = buf.readVariableInt();
        this.intendedProtocol = Protocol.byId(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.protocolVersion);
        buf.writeVariableInt(this.intendedProtocol.getStateId());
    }

    @Override
    public void handle(ServerHandshakePacketListener listener) {
        listener.onHandshake(this);
    }

    public Protocol getIntendedProtocol() {
        return this.intendedProtocol;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    @Nullable
    @Override
    public Protocol nextProtocol() {
        return Protocol.LOGIN;
    }
}
