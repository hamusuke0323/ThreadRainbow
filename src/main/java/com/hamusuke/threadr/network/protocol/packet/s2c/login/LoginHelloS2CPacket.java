package com.hamusuke.threadr.network.protocol.packet.s2c.login;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.security.PublicKey;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {
    private final String serverId;
    private final byte[] publicKey;
    private final byte[] nonce;

    public LoginHelloS2CPacket(String serverId, byte[] publicKey, byte[] nonce) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.nonce = nonce;
    }

    public LoginHelloS2CPacket(IntelligentByteBuf buf) {
        this.serverId = buf.readString(20);
        this.publicKey = buf.readByteArray();
        this.nonce = buf.readByteArray();
    }

    public void write(IntelligentByteBuf buf) {
        buf.writeString(this.serverId);
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.nonce);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onHello(this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() throws Exception {
        return NetworkEncryptionUtil.readEncodedPublicKey(this.publicKey);
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}
