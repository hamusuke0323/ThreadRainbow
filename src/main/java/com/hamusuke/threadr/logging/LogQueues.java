package com.hamusuke.threadr.logging;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogQueues {
    private static final Map<String, BlockingQueue<String>> QUEUES = new HashMap<>();
    private static final ReentrantReadWriteLock QUEUE_LOCK = new ReentrantReadWriteLock();

    public static BlockingQueue<String> getOrCreateQueue(String target) {
        try {
            QUEUE_LOCK.readLock().lock();
            var queue = QUEUES.get(target);
            if (queue != null) {
                return queue;
            }
        } finally {
            QUEUE_LOCK.readLock().unlock();
        }

        try {
            QUEUE_LOCK.writeLock().lock();
            return QUEUES.computeIfAbsent(target, s -> new LinkedBlockingQueue<>());
        } finally {
            QUEUE_LOCK.writeLock().unlock();
        }
    }

    @Nullable
    public static String getNextLogEvent(String queueName) {
        QUEUE_LOCK.readLock().lock();
        var queue = QUEUES.get(queueName);
        QUEUE_LOCK.readLock().unlock();

        if (queue != null) {
            try {
                return queue.take();
            } catch (InterruptedException ignored) {
            }
        }

        return null;
    }
}
