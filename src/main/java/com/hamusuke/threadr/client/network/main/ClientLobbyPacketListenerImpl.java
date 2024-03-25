package com.hamusuke.threadr.client.network.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.ChangeHostS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.lobby.StartGameS2CPacket;

public class ClientLobbyPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientLobbyPacketListener {
    public MainWindow mainWindow;

    public ClientLobbyPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleChangeHost(ChangeHostS2CPacket packet) {
        super.handleChangeHost(packet);

        if (this.client.getCurrentWindow() instanceof MainWindow w) {
            w.showStartButton(this.clientSpider.getId() == packet.getId());
        }
    }

    @Override
    public void handleStartGame(StartGameS2CPacket packet) {
        var listener = new ClientPlayPacketListenerImpl(this.client, this.connection);
        listener.mainWindow = this.mainWindow;
        this.mainWindow.rmLobby();
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }
}
