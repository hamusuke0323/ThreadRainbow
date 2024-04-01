package com.hamusuke.threadr.client.network.listener.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.StartGameNotify;

public class ClientLobbyPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientLobbyPacketListener {
    public MainWindow mainWindow;

    public ClientLobbyPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        super.handleChangeHost(packet);
        this.mainWindow.onChangeHost();
    }

    @Override
    public void handleStartGame(StartGameNotify packet) {
        int id = this.hostId;
        var listener = new ClientPlayPacketListenerImpl(this.client, this.connection);
        listener.hostId = id;
        listener.mainWindow = this.mainWindow;
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }
}
