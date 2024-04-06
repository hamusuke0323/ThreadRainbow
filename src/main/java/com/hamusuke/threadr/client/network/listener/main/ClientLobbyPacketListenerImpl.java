package com.hamusuke.threadr.client.network.listener.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.lobby.LobbyPanel;
import com.hamusuke.threadr.client.gui.component.panel.pre.ServerListPanel;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.RoomListNotify;

public class ClientLobbyPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientLobbyPacketListener {
    public ClientLobbyPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleRoomList(RoomListNotify packet) {
        if (this.client.getPanel() instanceof LobbyPanel panel) {
            panel.addAll(packet.infoList());
        }
    }

    @Override
    public void onDisconnected(String msg) {
        this.client.disconnect();
        var list = new ServerListPanel();
        var panel = msg.isEmpty() ? list : new OkPanel(list, "エラー", msg);
        this.client.getMainWindow().reset();
        this.client.setPanel(panel);
        this.client.clientSpider = null;
    }
}
