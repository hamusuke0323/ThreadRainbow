package com.hamusuke.threadr.client.network.listener.lobby;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.ServerListPanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.EnterPasswordPanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.threadr.client.gui.component.panel.lobby.LobbyPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.threadr.client.gui.component.table.SpiderTable;
import com.hamusuke.threadr.client.network.Chat;
import com.hamusuke.threadr.client.network.listener.main.ClientRoomPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.room.ClientRoom;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.*;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.EnterPasswordRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.LobbyPingReq;

import javax.swing.*;

public class ClientLobbyPacketListenerImpl implements ClientLobbyPacketListener {
    private final ThreadRainbowClient client;
    private final Connection connection;
    private final LocalSpider clientSpider;
    private int tickCount;

    public ClientLobbyPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        this.client = client;
        this.connection = connection;
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void tick() {
        this.tickCount++;
        if (this.tickCount % 20 == 0) {
            this.connection.sendPacket(new LobbyPingReq());
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void handleDisconnectPacket(LobbyDisconnectNotify packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handlePong(LobbyPongRsp packet) {
    }

    @Override
    public void handleRoomList(RoomListNotify packet) {
        if (this.client.getPanel() instanceof LobbyPanel panel) {
            panel.addAll(packet.infoList());
        }
    }

    @Override
    public void handleJoinRoomSucc(JoinRoomSuccNotify packet) {
        this.client.curRoom = ClientRoom.fromRoomInfo(packet.info());
        this.client.curRoom.join(this.clientSpider);
        this.client.spiderTable = new SpiderTable(this.client);
        SwingUtilities.invokeLater(this.client.spiderTable::clear);
        this.client.chat = new Chat(this.client);
        this.client.setPanel(new RoomPanel());
        var listener = new ClientRoomPacketListenerImpl(this.client, this.connection);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }

    @Override
    public void handleJoinRoomFail(JoinRoomFailNotify packet) {
        this.client.setPanel(new OkPanel(new LobbyPanel(), "エラー", packet.msg()));
    }

    @Override
    public void handleEnterPassword(EnterPasswordReq packet) {
        var enterPasswordPanel = new EnterPasswordPanel(p -> {
            if (!p.isAccepted()) {
                this.client.setPanel(new LobbyPanel());
                return;
            }

            this.client.setPanel(new CenteredMessagePanel("部屋に参加しています..."));
            this.connection.sendPacket(new EnterPasswordRsp(packet.roomId(), p.getPassword()));
        });
        var panel = packet.msg().isEmpty() ? enterPasswordPanel : new OkPanel(enterPasswordPanel, "エラー", packet.msg());
        this.client.setPanel(panel);
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
