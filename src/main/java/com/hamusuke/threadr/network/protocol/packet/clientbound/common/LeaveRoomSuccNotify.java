package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record LeaveRoomSuccNotify() implements Packet<ClientCommonPacketListener> {
    public LeaveRoomSuccNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeaveRoomSucc(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.LOBBY;
    }
}
