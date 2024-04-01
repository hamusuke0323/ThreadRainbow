package com.hamusuke.threadr.network.listener.client.login;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.*;

public interface ClientLoginPacketListener extends PacketListener {
    void handleKeyEx(KeyExchangeRsp packet);

    void handleSuccess(LoginSuccessNotify packet);

    void handleDisconnect(LoginDisconnectNotify packet);

    void handleCompression(LoginCompressionNotify packet);

    void handleEnterName(EnterNameReq packet);
}
