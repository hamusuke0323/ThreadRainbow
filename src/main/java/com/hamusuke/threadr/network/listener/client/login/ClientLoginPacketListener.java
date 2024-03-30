package com.hamusuke.threadr.network.listener.client.login;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.login.*;

public interface ClientLoginPacketListener extends PacketListener {
    void handleHello(LoginHelloS2CPacket packet);

    void handleSuccess(LoginSuccessS2CPacket packet);

    void handleDisconnect(LoginDisconnectS2CPacket packet);

    void handleCompression(LoginCompressionS2CPacket packet);

    void handleEnterName(EnterNameS2CPacket packet);
}
