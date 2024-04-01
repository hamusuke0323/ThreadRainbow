package com.hamusuke.threadr.network.listener.server.login;

import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.AliveReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EncryptionSetupReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EnterNameRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.KeyExchangeReq;

public interface ServerLoginPacketListener extends ServerPacketListener {
    void handleKeyEx(KeyExchangeReq packet);

    void handleEncryption(EncryptionSetupReq packet);

    void handlePing(AliveReq packet);

    void handleLogin(EnterNameRsp packet);
}
