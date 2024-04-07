package com.hamusuke.threadr.network.protocol.packet.clientbound.lobby;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record EnterPasswordReq(int roomId, String msg) implements Packet<ClientLobbyPacketListener> {
    public EnterPasswordReq(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.roomId);
        buf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleEnterPassword(this);
    }
}
