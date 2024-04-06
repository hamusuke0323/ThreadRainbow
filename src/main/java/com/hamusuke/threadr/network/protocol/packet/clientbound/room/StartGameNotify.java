package com.hamusuke.threadr.network.protocol.packet.clientbound.room;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record StartGameNotify() implements Packet<ClientRoomPacketListener> {
    public StartGameNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientRoomPacketListener listener) {
        listener.handleStartGame(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.PLAY;
    }
}
