package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.room.Room;

public record RoomListQueryReq(String query) implements Packet<ServerLobbyPacketListener> {
    public RoomListQueryReq(IntelligentByteBuf buf) {
        this(buf.readString(Room.MAX_ROOM_NAME_LENGTH));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeString(this.query, Room.MAX_ROOM_NAME_LENGTH);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleRoomListQuery(this);
    }
}
