package com.hamusuke.threadr.network.protocol.packet.serverbound.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public record EncryptionSetupReq(byte[] encryptedSecretKey,
                                 byte[] encryptedNonce) implements Packet<ServerLoginPacketListener> {
    public EncryptionSetupReq(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws Exception {
        this(NetworkEncryptionUtil.encrypt(publicKey, secretKey.getEncoded()), NetworkEncryptionUtil.encrypt(publicKey, nonce));
    }

    public EncryptionSetupReq(IntelligentByteBuf buf) {
        this(buf.readByteArray(), buf.readByteArray());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeByteArray(this.encryptedSecretKey);
        buf.writeByteArray(this.encryptedNonce);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleEncryption(this);
    }

    public SecretKey decryptSecretKey(PrivateKey privateKey) throws Exception {
        return NetworkEncryptionUtil.decryptSecretKey(privateKey, this.encryptedSecretKey);
    }

    public byte[] decryptNonce(PrivateKey privateKey) throws Exception {
        return NetworkEncryptionUtil.decrypt(privateKey, this.encryptedNonce);
    }
}
