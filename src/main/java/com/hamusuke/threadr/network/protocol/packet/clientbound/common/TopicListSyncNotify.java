package com.hamusuke.threadr.network.protocol.packet.clientbound.common;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TopicListSyncNotify() implements Packet<ClientCommonPacketListener> {
    @Override
    public void write(IntelligentByteBuf buf) {

    }

    @Override
    public void handle(ClientCommonPacketListener listener) {

    }
}
