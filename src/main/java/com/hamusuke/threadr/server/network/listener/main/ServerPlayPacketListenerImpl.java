package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket.Command;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.MoveCardC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.ExitGameSuccS2CPacket;
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
    public void handleClientCommand(ClientCommandC2SPacket packet) {
        if (this.server.getGame() == null) {
            LOGGER.warn("Illegal command packet came from client");
            return;
        }

        if (!this.server.isHost(this.spider) && packet.command() != Command.EXIT) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        switch (packet.command()) {
            case START_SELECTING_TOPIC -> this.server.getGame().startSelectingTopic();
            case RESELECT_TOPIC -> this.server.getGame().reselectTopic();
            case DECIDE_TOPIC -> this.server.getGame().decideTopic();
            case FINISH -> this.server.getGame().finish();
            case UNCOVER -> this.server.getGame().uncover();
            case RESTART -> this.server.getGame().restart();
            case EXIT -> {
                this.server.getGame().onSpiderLeft(this.spider);
                new ServerLobbyPacketListenerImpl(this.server, this.connection, this.spider);
                this.spider.sendPacket(new ExitGameSuccS2CPacket());
            }
            default -> this.spider.sendError("Illegal client command: " + packet.command());
        }
    }

    @Override
    public void handleMoveCard(MoveCardC2SPacket packet) {
        if (this.server.getGame() == null) {
            LOGGER.warn("Illegal command packet came from client");
            return;
        }

        this.server.getGame().moveCard(packet.from(), packet.to());
    }

    @Override
    public void onDisconnected(String msg) {
        super.onDisconnected(msg);

        if (this.server.getGame() != null) {
            this.server.getGame().onSpiderLeft(this.spider);
        }
    }
}