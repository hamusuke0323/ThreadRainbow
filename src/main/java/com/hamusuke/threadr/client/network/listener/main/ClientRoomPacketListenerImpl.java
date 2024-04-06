package com.hamusuke.threadr.client.network.listener.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.StartGameNotify;

public class ClientRoomPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientRoomPacketListener {
    public ClientRoomPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        super.handleChangeHost(packet);
        this.client.setPanel(this.client.getPanel());
    }

    @Override
    public void handleStartGame(StartGameNotify packet) {
        int id = this.hostId;
        var listener = new ClientPlayPacketListenerImpl(this.client, this.connection);
        listener.hostId = id;
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }
}
