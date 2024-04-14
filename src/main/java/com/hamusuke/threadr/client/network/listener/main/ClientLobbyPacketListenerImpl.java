package com.hamusuke.threadr.client.network.listener.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.EnterPasswordPanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.lobby.LobbyPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.threadr.client.gui.component.table.SpiderTable;
import com.hamusuke.threadr.client.network.Chat;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.*;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.EnterPasswordReq;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.JoinRoomFailNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.JoinRoomSuccNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.RoomListNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.EnterPasswordRsp;

import javax.swing.*;

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
    public void handleJoinRoomSucc(JoinRoomSuccNotify packet) {
        this.client.clientSpiders.clear();
        this.client.clientSpiders.add(this.clientSpider);
        this.client.spiderTable = new SpiderTable(this.client);
        SwingUtilities.invokeLater(this.client.spiderTable::clear);
        this.client.chat = new Chat(this.client);
        this.client.curRoom = packet.info();
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
    public void handleChangeHost(ChangeHostNotify packet) {
    }

    @Override
    public void handleChatPacket(ChatNotify packet) {
    }

    @Override
    public void handleJoinPacket(SpiderJoinNotify packet) {
    }

    @Override
    public void handleLeavePacket(SpiderLeaveNotify packet) {
    }

    @Override
    public void handleLeaveRoomSucc(LeaveRoomSuccNotify packet) {
    }
}
