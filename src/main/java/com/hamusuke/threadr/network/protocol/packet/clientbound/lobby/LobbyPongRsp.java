package com.hamusuke.threadr.network.protocol.packet.clientbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LobbyPongRsp() implements Packet<ClientLobbyPacketListener> {
    public LobbyPongRsp(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handlePong(this);
    }
}
