package com.hamusuke.threadr.network.listener.server;

import com.hamusuke.threadr.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.SpiderLoginC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginKeyC2SPacket;

public interface ServerLoginPacketListener extends ServerPacketListener {
    void onHello(LoginHelloC2SPacket packet);

    void onKey(LoginKeyC2SPacket packet);

    void onPing(AliveC2SPacket packet);

    void onLogin(SpiderLoginC2SPacket packet);
}
