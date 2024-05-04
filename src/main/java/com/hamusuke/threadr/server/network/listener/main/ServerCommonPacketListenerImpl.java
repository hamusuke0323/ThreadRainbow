package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.LeaveRoomSuccNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.PongRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.RTTChangeNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.*;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.lobby.ServerLobbyPacketListenerImpl;
import com.hamusuke.threadr.server.room.ServerRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public final Connection connection;
    protected final ThreadRainbowServer server;
    public final ServerSpider spider;
    @Nullable
    public final ServerRoom room;

    protected ServerCommonPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        this.server = server;
        this.connection = connection;
        connection.setListener(this);
        this.spider = spider;
        this.room = this.spider.currentRoom;
        spider.connection = this;
    }

    @Override
    public void handleLeaveRoom(LeaveRoomReq packet) {
        if (this.room != null) {
            this.room.leave(this.spider);
            this.spider.sendPacket(new LeaveRoomSuccNotify());
            new ServerLobbyPacketListenerImpl(this.server, this.connection, this.spider);
        }
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

        if (this.room != null) {
            this.room.sendPacketToAllInRoom(new ChatNotify(String.format("<%s> %s", this.spider.getName(), packet.msg())));
        }
    }

    @Override
    public void handlePingPacket(PingReq packet) {
        this.connection.sendPacket(new PongRsp(packet.clientTime()));
    }

    @Override
    public void handleRTTPacket(RTTChangeReq packet) {
        this.spider.setPing(packet.rtt());

        if (this.room != null) {
            this.room.sendPacketToAllInRoom(new RTTChangeNotify(this.spider.getId(), packet.rtt()));
        }
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.connection.getAddress());

        if (this.room != null) {
            this.room.leave(this.spider);
        }
        this.server.getSpiderManager().removeSpider(this.spider);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
