package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record JoinRoomReq(int id) implements Packet<ServerLobbyPacketListener> {
    public JoinRoomReq(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleJoinRoom(this);
    }
}
