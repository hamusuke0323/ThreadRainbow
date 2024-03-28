package com.hamusuke.threadr.network.protocol.packet.s2c.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record StartGameS2CPacket() implements Packet<ClientLobbyPacketListener> {
    public StartGameS2CPacket(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleStartGame(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.PLAY;
    }
}
