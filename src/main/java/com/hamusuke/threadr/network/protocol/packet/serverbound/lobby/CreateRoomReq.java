package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record CreateRoomReq(String roomName, String password) implements Packet<ServerLobbyPacketListener> {
    public CreateRoomReq(IntelligentByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeString(this.roomName);
        buf.writeString(this.password);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleCreateRoom(this);
    }
}
