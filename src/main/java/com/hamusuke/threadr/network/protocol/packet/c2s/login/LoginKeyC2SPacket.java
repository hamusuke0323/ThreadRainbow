package com.hamusuke.threadr.network.protocol.packet.c2s.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public record LoginKeyC2SPacket(byte[] encryptedSecretKey,
                                byte[] encryptedNonce) implements Packet<ServerLoginPacketListener> {
    public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws Exception {
        this(NetworkEncryptionUtil.encrypt(publicKey, secretKey.getEncoded()), NetworkEncryptionUtil.encrypt(publicKey, nonce));
    }

    public LoginKeyC2SPacket(IntelligentByteBuf buf) {
        this(buf.readByteArray(), buf.readByteArray());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeByteArray(this.encryptedSecretKey);
        buf.writeByteArray(this.encryptedNonce);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleKey(this);
    }

    public SecretKey decryptSecretKey(PrivateKey privateKey) throws Exception {
        return NetworkEncryptionUtil.decryptSecretKey(privateKey, this.encryptedSecretKey);
    }

    public byte[] decryptNonce(PrivateKey privateKey) throws Exception {
        return NetworkEncryptionUtil.decrypt(privateKey, this.encryptedNonce);
    }
}
