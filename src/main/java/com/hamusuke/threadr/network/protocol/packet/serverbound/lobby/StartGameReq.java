package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record StartGameReq() implements Packet<ServerLobbyPacketListener> {
    public StartGameReq(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleStartGame(this);
    }
}
