package com.hamusuke.threadr.network.protocol.packet.serverbound.room;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record StartGameReq() implements Packet<ServerRoomPacketListener> {
    public StartGameReq(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerRoomPacketListener listener) {
        listener.handleStartGame(this);
    }
}
