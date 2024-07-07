package com.hamusuke.threadr.server.network.listener.main.play;

import com.hamusuke.threadr.game.mode.SpidersThreadV2Game;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.ExitGameNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.ServerRoomPacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlay4SpidersThreadV2GamePacketListener extends ServerPlayPacketListenerImpl {
    private static final Logger LOGGER = LogManager.getLogger();
    private final SpidersThreadV2Game game;

    public ServerPlay4SpidersThreadV2GamePacketListener(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
        this.game = (SpidersThreadV2Game) this.commonGame;
    }

    @Override
    public void handleClientCommand(ClientCommandReq packet) {
        var com = packet.command();
        if (!this.spider.isHost() && com.isHostOnly()) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        switch (packet.command()) {
            case START_TOPIC_SELECTION -> this.game.startTopicSelection();
            case CHANGE_TOPIC -> this.game.changeTopic();
            case DECIDE_TOPIC -> this.game.decideTopic();
            case FINISH -> this.game.finish();
            case UNCOVER -> this.game.uncover();
            case RESTART -> this.game.restart();
            case EXIT -> {
                this.game.onSpiderLeft(this.spider);
                this.spider.sendPacket(new ExitGameNotify());
                new ServerRoomPacketListenerImpl(this.server, this.connection, this.spider);
            }
        }
    }

    @Override
    public void handleMoveCard(MoveCardReq packet) {
        this.game.moveCard(this.spider, packet.from(), packet.to());
    }
}
