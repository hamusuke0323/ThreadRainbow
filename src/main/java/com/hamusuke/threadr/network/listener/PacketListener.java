package com.hamusuke.threadr.network.listener;

import com.hamusuke.threadr.network.channel.Connection;

public interface PacketListener {
    void onDisconnected(String msg);

    Connection getConnection();

    default void tick() {
    }

    default boolean shouldCrashOnException() {
        return true;
    }
}
