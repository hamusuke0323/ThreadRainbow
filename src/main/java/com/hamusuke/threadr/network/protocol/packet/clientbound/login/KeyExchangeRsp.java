package com.hamusuke.threadr.network.protocol.packet.clientbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.security.PublicKey;

public record KeyExchangeRsp(byte[] publicKey,
                             byte[] nonce) implements Packet<ClientLoginPacketListener> {
    public KeyExchangeRsp(IntelligentByteBuf buf) {
        this(buf.readByteArray(), buf.readByteArray());
    }

    public void write(IntelligentByteBuf buf) {
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.nonce);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleKeyEx(this);
    }

    public PublicKey getPublicKey() throws Exception {
        return NetworkEncryptionUtil.readEncodedPublicKey(this.publicKey);
    }
}
