package com.hamusuke.threadr.util.thread;

public interface MessageListener<Msg> extends AutoCloseable {
    String getName();

    void sendMsg(Msg msg);

    @Override
    default void close() {
    }
}
