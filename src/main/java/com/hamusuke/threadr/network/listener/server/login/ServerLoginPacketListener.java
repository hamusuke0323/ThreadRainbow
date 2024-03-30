package com.hamusuke.threadr.network.listener.server.login;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginKeyC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.SpiderLoginC2SPacket;

public interface ServerLoginPacketListener extends ServerPacketListener {
    void handleHello(LoginHelloC2SPacket packet);

    void handleKey(LoginKeyC2SPacket packet);

    void handlePing(AliveC2SPacket packet);

    void handleLogin(SpiderLoginC2SPacket packet);
}
