package com.hamusuke.threadr.network.protocol.packet.clientbound.lobby;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.room.RoomInfo;

import java.util.List;

public record RoomListNotify(List<RoomInfo> infoList) implements Packet<ClientLobbyPacketListener> {
    public RoomListNotify(IntelligentByteBuf buf) {
        this(buf.<List<RoomInfo>, RoomInfo>readList(RoomInfo::new, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.infoList, RoomInfo::writeTo);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleRoomList(this);
    }
}
