package com.hamusuke.threadr.client.network.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.GiveCardS2CPacket;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    public MainWindow mainWindow;

    public ClientPlayPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
    }

    @Override
    public void handleGiveCard(GiveCardS2CPacket packet) {

    }
}
