package com.hamusuke.threadr.network.protocol.packet.clientbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LoginCompressionNotify(int threshold) implements Packet<ClientLoginPacketListener> {
    public LoginCompressionNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.threshold);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleCompression(this);
    }
}
