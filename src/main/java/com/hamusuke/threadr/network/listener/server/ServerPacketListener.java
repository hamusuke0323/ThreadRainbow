package com.hamusuke.threadr.network.listener.server;

import com.hamusuke.threadr.network.listener.PacketListener;

public interface ServerPacketListener extends PacketListener {
    @Override
    default boolean shouldCrashOnException() {
        return false;
    }
}
