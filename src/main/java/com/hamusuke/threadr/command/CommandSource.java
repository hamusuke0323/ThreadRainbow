package com.hamusuke.threadr.command;

import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;

import javax.annotation.Nullable;

public interface CommandSource {
    ThreadRainbowServer getServer();

    @Nullable
    ServerSpider getSender();

    String getDisplayName();

    default void sendMessageToAll(String msg) {
        this.sendMessage(msg, true);
    }

    default void sendFeedback(String msg) {
        this.sendCommandFeedback(msg, true);
    }

    default void sendError(String msg) {
        this.sendCommandFeedback(msg, false);
    }

    void sendCommandFeedback(String msg, boolean all);

    void sendMessage(String msg, boolean all);
}
