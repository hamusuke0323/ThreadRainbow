package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.*;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.*;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.lobby.ServerLobbyPacketListenerImpl;
import com.hamusuke.threadr.server.room.ServerRoom;
import com.hamusuke.threadr.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIMEOUT_TICKS = 600;
    public final Connection connection;
    protected final ThreadRainbowServer server;
    public final ServerSpider spider;
    public final ServerRoom room;
    private int timeoutTicks;
    private int tickCount;

    protected ServerCommonPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        this.server = server;
        this.connection = connection;
        connection.setListener(this);
        this.spider = spider;
        this.room = this.spider.curRoom;
        spider.connection = this;
    }

    @Override
    public void tick() {
        this.tickCount++;
        this.timeoutTicks++;

        if (this.timeoutTicks >= TIMEOUT_TICKS) {
            this.disconnect("タイムアウトしました");
        }

        if (this.tickCount % 20 == 0) {
            this.connection.sendPacket(new PingReq(Util.getMeasuringTimeMs()));
        }
    }

    private void disconnect(String msg) {
        try {
            this.connection.sendPacket(new DisconnectNotify(msg));
            this.connection.disconnect(msg);
        } catch (Exception e) {
            LOGGER.warn("Disconnect failed", e);
        }
    }

    @Override
    public void handleGetAllTopics(GetAllTopicsReq packet) {
        this.connection.sendPacket(new GetAllTopicsRsp(this.room.getTopicList().getTopicEntries()));
    }

    @Override
    public void handleLeaveRoom(LeaveRoomReq packet) {
        this.room.leave(this.spider);
        this.spider.sendPacket(new LeaveRoomSuccNotify());
        new ServerLobbyPacketListenerImpl(this.server, this.connection, this.spider);
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

        this.room.sendPacketToAllInRoom(new ChatNotify(String.format("<%s> %s", this.spider.getName(), packet.msg())));
    }

    @Override
    public void handlePongPacket(PongRsp packet) {
        this.timeoutTicks = 0;
        this.spider.setPing((int) (Util.getMeasuringTimeMs() - packet.serverTime()));
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.connection.getAddress());

        this.room.leave(this.spider);
        this.server.getSpiderManager().removeSpider(this.spider);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
