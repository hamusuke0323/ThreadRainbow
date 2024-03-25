package com.hamusuke.threadr.client.network.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.GiveLocalCardS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.RemoteCardGivenS2CPacket;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    public MainWindow mainWindow;

    public ClientPlayPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
    }

    @Override
    public void handleGiveCard(GiveLocalCardS2CPacket packet) {

    }

    @Override
    public void handleRemoteCard(RemoteCardGivenS2CPacket packet) {

    }
}
