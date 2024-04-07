package com.hamusuke.threadr.network.protocol.packet.clientbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record JoinRoomFailNotify(String msg) implements Packet<ClientLobbyPacketListener> {
    public JoinRoomFailNotify(IntelligentByteBuf buf) {
        this(buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleJoinRoomFail(this);
    }
}
