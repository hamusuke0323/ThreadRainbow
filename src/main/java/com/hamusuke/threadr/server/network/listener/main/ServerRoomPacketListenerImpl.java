package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.DisconnectNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.StartGameReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;

public class ServerRoomPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerRoomPacketListener {
    public ServerRoomPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
    }

    @Override
    public void handleStartGame(StartGameReq packet) {
        if (!this.spider.isHost()) {
            this.spider.sendError("ホストのみ操作できます");
            return;
        }

        if (this.room == null) {
            var msg = "あなたはどこの部屋に入っていません。\n不正なパケットを受信しました。";
            this.connection.sendPacket(new DisconnectNotify(msg), future -> this.connection.disconnect(msg));
        } else {
            this.room.startGame();
        }
    }
}
