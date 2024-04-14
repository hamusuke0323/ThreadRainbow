package com.hamusuke.threadr.network.protocol.packet.clientbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.room.RoomInfo;

public record JoinRoomSuccNotify(RoomInfo info) implements Packet<ClientLobbyPacketListener> {
    public JoinRoomSuccNotify(IntelligentByteBuf buf) {
        this(new RoomInfo(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.info.writeTo(buf);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleJoinRoomSucc(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.ROOM;
    }
}
