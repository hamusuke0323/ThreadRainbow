package com.hamusuke.threadr.server.network.listener.main.play;

import com.hamusuke.threadr.game.mode.Game;
import com.hamusuke.threadr.game.mode.GameMode;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ChooseTopicReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.TeamToggleReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.ServerCommonPacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public abstract class ServerPlayPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Game commonGame;

    public ServerPlayPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
        this.commonGame = Objects.requireNonNull(Objects.requireNonNull(spider.curRoom).getGame());
    }

    public static void newListenerByGameMode(GameMode gameMode, ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        switch (gameMode) {
            case SPIDERS_THREAD_V2 -> new ServerPlay4SpidersThreadV2GamePacketListener(server, connection, spider);
            case THREAD_RAINBOW -> new ServerPlay4ThreadRainbowGamePacketListener(server, connection, spider);
        }
    }

    @Override
    public void handleClientCommand(ClientCommandReq packet) {
        throw new UnsupportedOperationException("this operation is not supported on the game.");
    }

    @Override
    public void handleMoveCard(MoveCardReq packet) {
        throw new UnsupportedOperationException("this operation is not supported on the game.");
    }

    @Override
    public void handleChooseTopic(ChooseTopicReq packet) {
        if (!this.spider.isHost()) {
            this.spider.sendError("ホストのみお題を選択できます");
            return;
        }

        this.commonGame.setTopic(packet.topicId());
    }

    @Override
    public void handleTeamToggle(TeamToggleReq packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void onDisconnected(String msg) {
        super.onDisconnected(msg);
        this.commonGame.onSpiderLeft(this.spider);
    }
}
