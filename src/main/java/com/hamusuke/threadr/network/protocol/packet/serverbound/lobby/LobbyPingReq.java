package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LobbyPingReq() implements Packet<ServerLobbyPacketListener> {
    public LobbyPingReq(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handlePing(this);
    }
}
