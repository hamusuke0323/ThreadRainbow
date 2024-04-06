package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record RoomListReq() implements Packet<ServerLobbyPacketListener> {
    public RoomListReq(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleRoomList(this);
    }
}
