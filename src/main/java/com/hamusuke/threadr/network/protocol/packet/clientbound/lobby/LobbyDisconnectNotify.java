package com.hamusuke.threadr.network.protocol.packet.clientbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LobbyDisconnectNotify(String msg) implements Packet<ClientLobbyPacketListener> {
    public LobbyDisconnectNotify(IntelligentByteBuf buf) {
        this(buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeString(msg);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }
}
