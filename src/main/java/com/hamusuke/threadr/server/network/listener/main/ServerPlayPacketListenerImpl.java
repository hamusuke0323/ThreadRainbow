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
        if (this.server.getGame() == null) {
            LOGGER.warn("Illegal command packet came from client");
            return;
        }

        var com = packet.command();
        if (!this.server.isHost(this.spider) && com.isHostOnly()) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        switch (packet.command()) {
            case START_TOPIC_SELECTION -> this.server.getGame().startTopicSelection();
            case CHANGE_TOPIC -> this.server.getGame().changeTopic();
            case DECIDE_TOPIC -> this.server.getGame().decideTopic();
            case FINISH -> this.server.getGame().finish();
            case UNCOVER -> this.server.getGame().uncover();
            case RESTART -> this.server.getGame().restart();
            case EXIT -> {
                this.server.getGame().onSpiderLeft(this.spider);
                new ServerLobbyPacketListenerImpl(this.server, this.connection, this.spider);
                this.spider.sendPacket(new ExitGameNotify());
            }
        }
    }

    @Override
    public void handleMoveCard(MoveCardReq packet) {
        if (this.server.getGame() == null) {
            LOGGER.warn("Illegal command packet came from client");
            return;
        }

        this.server.getGame().moveCard(this.spider, packet.from(), packet.to());
    }

    @Override
    public void onDisconnected(String msg) {
        super.onDisconnected(msg);

        if (this.server.getGame() != null) {
            this.server.getGame().onSpiderLeft(this.spider);
        }
    }
}
