package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public class LoginCompressionS2CPacket implements Packet<ClientLoginPacketListener> {
    private final int threshold;

    public LoginCompressionS2CPacket(int threshold) {
        this.threshold = threshold;
    }

    public LoginCompressionS2CPacket(IntelligentByteBuf byteBuf) {
        this.threshold = byteBuf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.threshold);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onCompression(this);
    }

    public int getThreshold() {
        return this.threshold;
    }
}
