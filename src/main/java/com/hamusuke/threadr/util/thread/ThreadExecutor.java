package com.hamusuke.threadr.util.thread;

import com.google.common.collect.Queues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class ThreadExecutor<R extends Runnable> implements MessageListener<R>, Executor {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String name;
    private final Queue<R> tasks = Queues.newConcurrentLinkedQueue();
    private int executionsInProgress;

    protected ThreadExecutor(String name) {
        this.name = name;
    }

    protected abstract R createTask(Runnable runnable);

    protected abstract boolean canExecute(R task);

    public boolean isSameThread() {
        return Thread.currentThread() == this.getThread();
    }

    protected abstract Thread getThread();

    protected boolean shouldExecuteAsync() {
        return !this.isSameThread();
    }

    public int getTaskCount() {
        return this.tasks.size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public <V> CompletableFuture<V> submit(Supplier<V> task) {
        return this.shouldExecuteAsync() ? CompletableFuture.supplyAsync(task, this) : CompletableFuture.completedFuture(task.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable task) {
        if (this.shouldExecuteAsync()) {
            return this.submitAsync(task);
        } else {
            task.run();
            return CompletableFuture.completedFuture(null);
        }
    }

    public void submitAndJoin(Runnable runnable) {
        if (!this.isSameThread()) {
            this.submitAsync(runnable).join();
        } else {
            runnable.run();
        }
    }

    @Override
    public void sendMsg(R runnable) {
        this.tasks.add(runnable);
        LockSupport.unpark(this.getThread());
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.shouldExecuteAsync()) {
            this.sendMsg(this.createTask(runnable));
        } else {
            runnable.run();
        }
    }

    public void executeSync(Runnable runnable) {
        this.execute(runnable);
    }

    protected void cancelTasks() {
        this.tasks.clear();
    }

    protected void runTasks() {
        while (this.runTask()) {
        }
    }

    public boolean runTask() {
        R runnable = this.tasks.peek();
        if (runnable == null) {
            return false;
        } else if (this.executionsInProgress == 0 && !this.canExecute(runnable)) {
            return false;
        } else {
            this.executeTask(this.tasks.remove());
            return true;
        }
    }

    public void runTasks(BooleanSupplier stopCondition) {
        ++this.executionsInProgress;

        try {
            while (!stopCondition.getAsBoolean()) {
                if (!this.runTask()) {
                    this.waitForTasks();
                }
            }
        } finally {
            --this.executionsInProgress;
        }
    }

    protected void waitForTasks() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void executeTask(R task) {
        try {
            task.run();
        } catch (Exception e) {
            LOGGER.fatal("Error executing task on " + this.getName(), e);
        }
    }
}
