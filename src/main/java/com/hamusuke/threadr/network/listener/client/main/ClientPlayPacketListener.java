package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.s2c.play.GiveLocalCardS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.RemoteCardGivenS2CPacket;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    void handleGiveCard(GiveLocalCardS2CPacket packet);

    void handleRemoteCard(RemoteCardGivenS2CPacket packet);
}
