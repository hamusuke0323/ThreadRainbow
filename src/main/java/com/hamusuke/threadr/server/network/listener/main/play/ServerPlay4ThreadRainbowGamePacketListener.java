package com.hamusuke.threadr.server.network.listener.main.play;

import com.hamusuke.threadr.game.mode.ThreadRainbowGame;
import com.hamusuke.threadr.game.mode.ThreadRainbowGame.Status;
import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.ExitGameNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.ChatReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.TeamToggleReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.game.team.ServerTeamEntry;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.ServerRoomPacketListenerImpl;

public class ServerPlay4ThreadRainbowGamePacketListener extends ServerPlayPacketListenerImpl {
    private final ThreadRainbowGame game;
    public TeamType myTeamType = TeamType.BLUE;

    public ServerPlay4ThreadRainbowGamePacketListener(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
        this.game = (ThreadRainbowGame) this.commonGame;
    }

    @Override
    public void handleClientCommand(ClientCommandReq packet) {
        var com = packet.command();
        if (!this.spider.isHost() && com.isHostOnly()) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        switch (com) {
            case FINISH_MAKING_TEAM -> this.game.finishMakingTeam();
            case CHANGE_TOPIC -> this.game.changeTopic();
            case DECIDE_TOPIC -> this.game.decideTopic();
            case START_MAIN_GAME -> this.game.startMainGame();
            case TEAM_FINISH -> this.game.onFinishBtnPressed(this.myTeamType, this.spider);
            case UNCOVER -> this.game.uncoverCard();
            case NEXT -> this.game.startResultingNextTeam();
            case RESTART -> this.game.restart();
            case RESTART_WITH_THE_SAME_TEAM -> this.game.restartWithTheSameTeam();
            case EXIT -> {
                this.game.onSpiderLeft(this.spider);
                this.spider.sendPacket(new ExitGameNotify());
                new ServerRoomPacketListenerImpl(this.server, this.connection, this.spider);
            }
        }
    }

    @Override
    public void handleTeamToggle(TeamToggleReq packet) {
        if (!this.spider.isHost()) {
            this.spider.sendError("ホストのみチーム分けできます");
            return;
        }

        if (this.room.getSpider(packet.teamSerializer().spiderId()) == null) {
            return;
        }

        this.game.toggleTeam(ServerTeamEntry.deserializeForServer(packet.teamSerializer(), this.room::getSpider));
    }

    @Override
    public void handleChatPacket(ChatReq packet) {
        if (this.game.getStatus() != Status.PLAYING || packet.msg().startsWith("/")) {
            super.handleChatPacket(packet);
            return;
        }

        var notify = new ChatNotify(String.format("<%s> %s", this.spider.getName(), packet.msg()));
        switch (this.myTeamType) {
            case BLUE -> this.game.getTeam().sendPacketToBlueTeam(notify);
            case RED -> this.game.getTeam().sendPacketToRedTeam(notify);
        }
    }

    @Override
    public void handleMoveCard(MoveCardReq packet) {
        this.game.moveCard(this.myTeamType, this.spider, packet.from(), packet.to());
    }
}
