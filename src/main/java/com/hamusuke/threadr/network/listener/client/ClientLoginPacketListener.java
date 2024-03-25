package com.hamusuke.threadr.network.listener.client;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.login.*;

public interface ClientLoginPacketListener extends PacketListener {
    void onHello(LoginHelloS2CPacket packet);

    void onSuccess(LoginSuccessS2CPacket packet);

    void onDisconnect(LoginDisconnectS2CPacket packet);

    void onCompression(LoginCompressionS2CPacket packet);

    void onEnterName(EnterNameS2CPacket packet);
}
