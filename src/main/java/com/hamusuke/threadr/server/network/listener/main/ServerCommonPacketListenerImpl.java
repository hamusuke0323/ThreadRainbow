package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.PongRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.RTTChangeNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.SpiderLeaveNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.ChatReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.DisconnectReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.PingReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.RTTChangeReq;
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
    public void handleDisconnect(DisconnectReq packet) {
        this.connection.disconnect("");
    }

    @Override
    public void handleChatPacket(ChatReq packet) {
        if (packet.msg().startsWith("/")) {
            this.server.runCommand(this.spider, packet.msg().substring(1));

            return;
        }

        this.server.sendPacketToAll(new ChatNotify(String.format("<%s> %s", this.spider.getName(), packet.msg())));
    }

    @Override
    public void handlePingPacket(PingReq packet) {
        this.connection.sendPacket(new PongRsp(packet.clientTime()));
    }

    @Override
    public void handleRTTPacket(RTTChangeReq packet) {
        this.spider.setPing(packet.rtt());
        this.server.sendPacketToAll(new RTTChangeNotify(this.spider.getId(), packet.rtt()));
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.connection.getAddress());
        this.spider.sendPacketToOthers(new SpiderLeaveNotify(this.spider.getId()));
        this.server.getSpiderManager().removeSpider(this.spider);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
