package com.hamusuke.threadr.network.protocol.packet.serverbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.room.Room;

public record EnterPasswordRsp(int roomId, String password) implements Packet<ServerLobbyPacketListener> {
    public EnterPasswordRsp(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readString(Room.MAX_ROOM_PASSWD_LENGTH));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.roomId);
        buf.writeString(this.password, Room.MAX_ROOM_PASSWD_LENGTH);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleEnterPassword(this);
    }
}
