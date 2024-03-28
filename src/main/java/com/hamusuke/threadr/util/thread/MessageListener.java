package com.hamusuke.threadr.util.thread;

import com.hamusuke.threadr.util.Either;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MessageListener<Msg> extends AutoCloseable {
    static <Msg> MessageListener<Msg> create(String name, Consumer<Msg> action) {
        return new MessageListener<>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void sendMsg(Msg message) {
                action.accept(message);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    String getName();

    void sendMsg(Msg msg);

    @Override
    default void close() {
    }

    default <Source> CompletableFuture<Source> ask(Function<? super MessageListener<Source>, ? extends Msg> messageProvider) {
        CompletableFuture<Source> completableFuture = new CompletableFuture<>();
        Objects.requireNonNull(completableFuture);
        Msg msg = messageProvider.apply(create("ask future processor handle", completableFuture::complete));
        this.sendMsg(msg);
        return completableFuture;
    }

    default <Source> CompletableFuture<Source> askFallible(Function<? super MessageListener<Either<Source, Exception>>, ? extends Msg> messageProvider) {
        CompletableFuture<Source> completableFuture = new CompletableFuture<>();
        Msg msg = messageProvider.apply(create("ask future processor handle", (either) -> {
            Objects.requireNonNull(completableFuture);
            either.ifLeftExists(completableFuture::complete);
            Objects.requireNonNull(completableFuture);
            either.ifRightExists(completableFuture::completeExceptionally);
        }));
        this.sendMsg(msg);
        return completableFuture;
    }
}
