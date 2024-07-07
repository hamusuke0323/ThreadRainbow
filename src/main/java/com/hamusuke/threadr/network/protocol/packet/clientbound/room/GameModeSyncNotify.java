package com.hamusuke.threadr.network.protocol.packet.clientbound.room;

import com.hamusuke.threadr.game.mode.GameMode;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record GameModeSyncNotify(GameMode gameMode) implements Packet<ClientRoomPacketListener> {
    public GameModeSyncNotify(IntelligentByteBuf buf) {
        this(buf.readEnum(GameMode.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.gameMode);
    }

    @Override
    public void handle(ClientRoomPacketListener listener) {
        listener.handleGameModeSync(this);
    }
}
