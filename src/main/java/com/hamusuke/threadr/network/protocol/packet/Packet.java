package com.hamusuke.threadr.network.protocol.packet;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;

import javax.annotation.Nullable;
import java.io.IOException;

public interface Packet<T extends PacketListener> {
    void write(IntelligentByteBuf buf) throws IOException;

    void handle(T listener);

    @Nullable
    default Protocol nextProtocol() {
        return null;
    }
}
