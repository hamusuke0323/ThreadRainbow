package com.hamusuke.threadr.network.protocol.packet.c2s.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import java.io.IOException;

public record StartGameC2SPacket() implements Packet<ServerLobbyPacketListener> {
    public StartGameC2SPacket(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleStartGame(this);
    }
}
