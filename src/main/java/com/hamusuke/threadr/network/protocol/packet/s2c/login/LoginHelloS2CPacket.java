package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.security.PublicKey;

public record LoginHelloS2CPacket(String serverId, byte[] publicKey,
                                  byte[] nonce) implements Packet<ClientLoginPacketListener> {
    public LoginHelloS2CPacket(IntelligentByteBuf buf) {
        this(buf.readString(20), buf.readByteArray(), buf.readByteArray());
    }

    public void write(IntelligentByteBuf buf) {
        buf.writeString(this.serverId);
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.nonce);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleHello(this);
    }

    public PublicKey getPublicKey() throws Exception {
        return NetworkEncryptionUtil.readEncodedPublicKey(this.publicKey);
    }
}
