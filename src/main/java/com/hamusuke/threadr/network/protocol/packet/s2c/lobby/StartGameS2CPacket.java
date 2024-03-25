package com.hamusuke.threadr.network.protocol.packet.s2c.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

import javax.annotation.Nullable;
import java.io.IOException;

public class StartGameS2CPacket implements Packet<ClientLobbyPacketListener> {
    public StartGameS2CPacket() {
    }

    public StartGameS2CPacket(IntelligentByteBuf buf) {
    }

    @Override
    public void write(IntelligentByteBuf buf) throws IOException {
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleStartGame(this);
    }

    @Nullable
    @Override
    public Protocol nextProtocol() {
        return Protocol.PLAY;
    }
}
