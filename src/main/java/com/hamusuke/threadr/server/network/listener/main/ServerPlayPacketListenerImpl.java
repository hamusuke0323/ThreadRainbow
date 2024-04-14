package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.ExitGameNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();

    public ServerPlayPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
    }

    @Override
    public void handleClientCommand(ClientCommandReq packet) {
        if (this.room == null || this.room.getGame() == null) {
            LOGGER.warn("Illegal command packet came from client");
            return;
        }

        var com = packet.command();
        if (!this.spider.isHost() && com.isHostOnly()) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        switch (packet.command()) {
            case START_TOPIC_SELECTION -> this.room.getGame().startTopicSelection();
            case CHANGE_TOPIC -> this.room.getGame().changeTopic();
            case DECIDE_TOPIC -> this.room.getGame().decideTopic();
            case FINISH -> this.room.getGame().finish();
            case UNCOVER -> this.room.getGame().uncover();
            case RESTART -> this.room.getGame().restart();
            case EXIT -> {
                this.room.getGame().onSpiderLeft(this.spider);
                this.spider.sendPacket(new ExitGameNotify());
                new ServerRoomPacketListenerImpl(this.server, this.connection, this.spider);
            }
        }
    }

    @Override
    public void handleMoveCard(MoveCardReq packet) {
        if (this.room == null || this.room.getGame() == null) {
            LOGGER.warn("Illegal move card packet came from client");
            return;
        }

        this.room.getGame().moveCard(this.spider, packet.from(), packet.to());
    }

    @Override
    public void onDisconnected(String msg) {
        super.onDisconnected(msg);

        if (this.room != null && this.room.getGame() != null) {
            this.room.getGame().onSpiderLeft(this.spider);
        }
    }
}
