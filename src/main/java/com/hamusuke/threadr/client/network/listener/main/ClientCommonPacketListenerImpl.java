package com.hamusuke.threadr.client.network.listener.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.ServerListPanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.threadr.client.gui.component.panel.lobby.LobbyPanel;
import com.hamusuke.threadr.client.network.listener.lobby.ClientLobbyPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.client.room.ClientRoom;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.*;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.PongRsp;

import javax.swing.*;
import java.util.Collections;

public abstract class ClientCommonPacketListenerImpl implements ClientCommonPacketListener {
    protected final Connection connection;
    protected final ThreadRainbowClient client;
    protected LocalSpider clientSpider;
    protected int tickCount;
    protected final ClientRoom curRoom;

    protected ClientCommonPacketListenerImpl(ThreadRainbowClient client, ClientRoom room, Connection connection) {
        this.client = client;
        this.curRoom = room;
        this.client.listener = this;
        this.connection = connection;
    }

    @Override
    public void tick() {
        this.tickCount++;
    }

    @Override
    public void handleGetAllTopics(GetAllTopicsRsp packet) {
        this.curRoom.getTopicList().syncWithServer(packet.topicEntries());
    }

    @Override
    public void handleGetTopic(GetTopicRsp packet) {
        this.curRoom.getTopicList().syncWithServer(Collections.singletonList(packet.topicEntry()));
    }

    @Override
    public void handleNewTopicAdd(NewTopicAddNotify packet) {
        this.curRoom.getTopicList().syncWithServer(Collections.singletonList(packet.newTopic()));
    }

    @Override
    public void handleRemoveTopic(RemoveTopicNotify packet) {
        this.curRoom.getTopicList().removeTopics(packet.removedTopicIds());
    }

    @Override
    public void handleChatPacket(ChatNotify packet) {
        this.client.chat.addMessage(packet.msg());
    }

    @Override
    public void handlePingPacket(PingReq packet) {
        if (!this.client.isSameThread()) {
            this.client.executeSync(() -> packet.handle(this));
        }

        this.connection.sendPacket(new PongRsp(packet.serverTime()));
    }

    @Override
    public void handleRTTPacket(RTTChangeNotify packet) {
        synchronized (this.curRoom.getSpiders()) {
            this.curRoom.getSpiders().stream()
                    .filter(p -> p.getId() == packet.spiderId())
                    .forEach(spider -> spider.setPing(packet.rtt()));
        }

        SwingUtilities.invokeLater(this.client.spiderTable::update);
    }

    @Override
    public void handleDisconnectPacket(DisconnectNotify packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handleJoinPacket(SpiderJoinNotify packet) {
        var spider = new RemoteSpider(packet.name());
        spider.setId(packet.id());
        this.curRoom.join(spider);
    }

    @Override
    public void handleLeavePacket(SpiderLeaveNotify packet) {
        this.curRoom.leave(packet.id());
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        synchronized (this.curRoom.getSpiders()) {
            this.curRoom.getSpiders().stream()
                    .filter(p -> p.getId() == packet.id())
                    .findFirst()
                    .ifPresent(this.curRoom::setHost);
        }

        if (this.client.amIHost()) {
            this.client.getMainWindow().topicListPanel.showButtons();
        } else {
            this.client.getMainWindow().topicListPanel.hideButtons();
        }
    }

    @Override
    public void handleLeaveRoomSucc(LeaveRoomSuccNotify packet) {
        this.client.getMainWindow().reset(false);
        this.client.topics.clear();
        var listener = new ClientLobbyPacketListenerImpl(this.client, this.connection);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
        this.client.setPanel(new LobbyPanel());
    }

    @Override
    public void onDisconnected(String msg) {
        this.client.disconnect();

        var list = new ServerListPanel();
        var panel = msg.isEmpty() ? list : new OkPanel(list, "エラー", msg);
        this.client.getMainWindow().reset();
        this.client.setPanel(panel);
        this.client.clientSpider = null;
        this.client.spiderTable = null;
        this.client.chat = null;
        this.client.curRoom = null;
        this.client.model = null;
    }

    public ThreadRainbowClient getClient() {
        return this.client;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
