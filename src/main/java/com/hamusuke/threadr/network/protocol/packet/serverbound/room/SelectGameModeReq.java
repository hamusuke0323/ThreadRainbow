package com.hamusuke.threadr.network.protocol.packet.serverbound.room;

import com.hamusuke.threadr.game.mode.GameMode;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record SelectGameModeReq(GameMode gameMode) implements Packet<ServerRoomPacketListener> {
    public SelectGameModeReq(IntelligentByteBuf buf) {
        this(buf.readEnum(GameMode.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.gameMode);
    }

    @Override
    public void handle(ServerRoomPacketListener listener) {
        listener.handleSelectGameRule(this);
    }
}
