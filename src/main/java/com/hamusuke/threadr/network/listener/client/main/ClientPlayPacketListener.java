package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.s2c.play.GiveCardS2CPacket;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    void handleGiveCard(GiveCardS2CPacket packet);
}
