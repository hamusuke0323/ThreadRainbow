package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.GameModeSyncNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.SelectGameModeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.StartGameReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;

public class ServerRoomPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerRoomPacketListener {
    public ServerRoomPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
        spider.sendPacket(new GameModeSyncNotify(this.room.curGameMode));
    }

    @Override
    public void handleStartGame(StartGameReq packet) {
        if (!this.spider.isHost()) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        this.room.startGame();
    }

    @Override
    public void handleSelectGameRule(SelectGameModeReq packet) {
        if (!this.spider.isHost()) {
            this.spider.sendError("ホストのみゲームルールを変更できます");
            return;
        }

        this.room.curGameMode = packet.gameMode();
        this.room.sendPacketToOthersInRoom(this.spider, new GameModeSyncNotify(this.room.curGameMode));
    }
}
