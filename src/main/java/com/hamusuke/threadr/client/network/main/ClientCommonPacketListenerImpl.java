package com.hamusuke.threadr.client.network.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.ConnectingWindow;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.PingC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.RTTC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.*;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;

public abstract class ClientCommonPacketListenerImpl implements ClientCommonPacketListener {
    protected final Connection connection;
    protected final ThreadRainbowClient client;
    protected LocalSpider clientSpider;
    protected int tickCount;
    protected int hostId;

    protected ClientCommonPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        this.client = client;
        this.client.listener = this;
        this.connection = connection;
    }

    @Override
    public void tick() {
        this.tickCount++;
        if (this.tickCount % 20 == 0) {
            this.connection.sendPacket(new PingC2SPacket(Util.getMeasuringTimeMs()));
        }
    }

    @Override
    public void handleChatPacket(ChatS2CPacket packet) {
        this.client.chat.addMessage(packet.msg());
    }

    @Override
    public void handlePongPacket(PongS2CPacket packet) {
        if (!this.client.isSameThread()) {
            this.client.executeSync(() -> packet.handle(this));
        }

        this.connection.sendPacket(new RTTC2SPacket((int) (Util.getMeasuringTimeMs() - packet.clientTime())));
    }

    @Override
    public void handleRTTPacket(RTTS2CPacket packet) {
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.stream().filter(p -> p.getId() == packet.spiderId()).forEach(spider -> {
                spider.setPing(packet.rtt());
            });
        }

        SwingUtilities.invokeLater(this.client.spiderTable::update);
    }

    @Override
    public void handleDisconnectPacket(DisconnectS2CPacket packet) {
        this.connection.disconnect();
    }

    @Override
    public void handleJoinPacket(JoinSpiderS2CPacket packet) {
        var spider = new RemoteSpider(packet.name());
        spider.setId(packet.id());
        this.client.addClientSpider(spider);
    }

    @Override
    public void handleLeavePacket(LeaveSpiderS2CPacket packet) {
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.removeIf(p -> p.getId() == packet.id());
        }
    }

    @Override
    public void handleChangeHost(ChangeHostS2CPacket packet) {
        this.hostId = packet.id();
    }

    public int getHostId() {
        return this.hostId;
    }

    @Override
    public void onDisconnected() {
        this.client.clientSpiders.clear();
        this.client.disconnect();
        var window = this.client.getCurrentWindow();
        if (window != null) {
            window.dispose();
        }
        this.client.setCurrentWindow(new ConnectingWindow());
        this.client.clientSpider = null;
        this.client.spiderTable = null;
        this.client.chat = null;
    }

    public ThreadRainbowClient getClient() {
        return this.client;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
