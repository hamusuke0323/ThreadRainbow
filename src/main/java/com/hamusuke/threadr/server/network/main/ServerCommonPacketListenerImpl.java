package com.hamusuke.threadr.server.network.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.ChatC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.DisconnectC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.PingC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.RTTC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.ChatS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.LeaveSpiderS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.PongS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.RTTS2CPacket;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public final Connection connection;
    protected final ThreadRainbowServer server;
    public final ServerSpider spider;

    protected ServerCommonPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        this.server = server;
        this.connection = connection;
        connection.setListener(this);
        this.spider = spider;
        spider.connection = this;
    }

    @Override
    public void handleDisconnect(DisconnectC2SPacket packet) {
        this.connection.disconnect("");
    }

    @Override
    public void handleChatPacket(ChatC2SPacket packet) {
        if (packet.msg().startsWith("/")) {
            this.server.runCommand(this.spider, packet.msg().substring(1));

            return;
        }

        this.server.sendPacketToAll(new ChatS2CPacket(String.format("<%s> %s", this.spider.getName(), packet.msg())));
    }

    @Override
    public void handlePingPacket(PingC2SPacket packet) {
        this.connection.sendPacket(new PongS2CPacket(packet.clientTime()));
    }

    @Override
    public void handleRTTPacket(RTTC2SPacket packet) {
        this.spider.setPing(packet.rtt());
        this.server.sendPacketToAll(new RTTS2CPacket(this.spider.getId(), packet.rtt()));
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.connection.getAddress());
        this.spider.sendPacketToOthers(new LeaveSpiderS2CPacket(this.spider.getId()));
        this.server.getSpiderManager().removeSpider(this.spider);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
