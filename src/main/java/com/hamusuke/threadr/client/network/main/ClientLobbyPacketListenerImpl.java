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

        this.mainWindow.showStartButton(this.clientSpider.getId() == packet.id());
    }

    @Override
    public void handleStartGame(StartGameS2CPacket packet) {
        int id = this.client.listener.hostId;
        var listener = new ClientPlayPacketListenerImpl(this.client, this.connection);
        listener.hostId = id;
        listener.mainWindow = this.mainWindow;
        this.mainWindow.rmLobby();
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }
}
