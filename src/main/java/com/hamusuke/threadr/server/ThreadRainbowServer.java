package com.hamusuke.threadr.server;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.command.Commands;
import com.hamusuke.threadr.game.mode.SpidersThreadV2Game;
import com.hamusuke.threadr.game.topic.TopicLoader;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.ChatS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.lobby.StartGameS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.RestartGameS2CPacket;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.ServerPlayPacketListenerImpl;
import com.hamusuke.threadr.util.Util;
import com.hamusuke.threadr.util.thread.ReentrantThreadExecutor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.KeyPair;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class ThreadRainbowServer extends ReentrantThreadExecutor<ServerTask> implements AutoCloseable, CommandSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerNetworkIo networkIo;
    private final AtomicBoolean running = new AtomicBoolean();
    private final Thread serverThread;
    private final Executor worker;
    protected final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private String serverIp;
    private int serverPort;
    private boolean stopped;
    private int ticks;
    private SpiderManager spiderManager;
    @Nullable
    private KeyPair keyPair;
    private boolean waitingForNextTick;
    private long nextTickTimestamp;
    private long timeReference;
    private long lastTimeReference;
    private final AtomicBoolean loading = new AtomicBoolean();
    private final TopicLoader topicLoader = new TopicLoader();
    @Nullable
    protected SpidersThreadV2Game game;

    public ThreadRainbowServer(Thread serverThread) {
        super("Server");
        this.serverPort = -1;
        this.running.set(true);
        this.networkIo = new ServerNetworkIo(this);
        this.serverThread = serverThread;
        this.worker = Util.getMainWorkerExecutor();
        this.setSpiderManager(new SpiderManager(this));
    }

    public static <S extends ThreadRainbowServer> S startServer(Function<Thread, S> factory) {
        AtomicReference<S> atomicReference = new AtomicReference<>();
        Thread thread = new Thread(() -> atomicReference.get().runServer(), "Server Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Error occurred in server thread", e));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            thread.setPriority(8);
        }

        S server = factory.apply(thread);
        atomicReference.set(server);
        thread.start();
        return server;
    }

    protected abstract boolean setupServer() throws IOException;

    protected void runServer() {
        try {
            this.topicLoader.loadTopics();
            Commands.registerCommands(this.dispatcher);
            if (this.setupServer()) {
                this.timeReference = Util.getMeasuringTimeMs();

                while (this.running.get()) {
                    long l = Util.getMeasuringTimeMs() - this.timeReference;
                    if (l > 2000L && this.timeReference - this.lastTimeReference >= 15000L) {
                        long m = l / 50L;
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
                        this.timeReference += m * 50L;
                        this.lastTimeReference = this.timeReference;
                    }

                    this.timeReference += 50L;
                    this.tick();
                    this.waitingForNextTick = true;
                    this.nextTickTimestamp = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
                    this.runTasksTillTickEnd();
                    if (!this.loading.get()) {
                        this.loading.set(true);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Encountered an unexpected exception", e);
        } finally {
            try {
                this.stopped = true;
                this.shutdown();
            } catch (Throwable e) {
                LOGGER.error("Error occurred while stopping the server", e);
            } finally {
                this.exit();
            }
        }
    }

    public void sendPacketToAll(Packet<?> packet) {
        this.sendPacketToAll(packet, null);
    }

    public void sendPacketToAll(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        this.getSpiderManager().sendPacketToAll(packet, callback);
    }

    public void tick() {
        this.ticks++;
        this.getNetworkIo().tick();

        if (this.game != null) {
            this.game.tick();
        }
    }

    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.waitingForNextTick ? this.nextTickTimestamp : this.timeReference);
    }

    protected void runTasksTillTickEnd() {
        this.runTasks();
        this.runTasks(() -> !this.shouldKeepTicking());
    }

    @Override
    protected ServerTask createTask(Runnable runnable) {
        return new ServerTask(this.ticks, runnable);
    }

    @Override
    protected boolean canExecute(ServerTask task) {
        return task.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
    }

    @Override
    public boolean runTask() {
        boolean bl = this.runOneTask();
        this.waitingForNextTick = bl;
        return bl;
    }

    public void runCommand(ServerSpider spider, String command) {
        try {
            this.dispatcher.execute(command, spider);
        } catch (CommandSyntaxException e) {
            spider.sendFeedback(e.getContext());
        }
    }

    private boolean runOneTask() {
        return super.runTask();
    }

    @Override
    public void close() {
        this.shutdown();
    }

    public void shutdown() {
        LOGGER.info("Stopping server");
        if (this.getNetworkIo() != null) {
            this.getNetworkIo().stop();
        }
    }

    @Override
    public ThreadRainbowServer getServer() {
        return this;
    }

    @Nullable
    @Override
    public ServerSpider getSender() {
        return null;
    }

    @Override
    public void sendMessage(String msg, boolean all) {
        LOGGER.info(msg);

        if (all) {
            this.sendPacketToAll(new ChatS2CPacket(String.format("[%s] %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public void sendCommandFeedback(String msg, boolean all) {
        LOGGER.info(msg);

        if (all) {
            this.sendPacketToAll(new ChatS2CPacket(String.format("[%s]: %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public String getDisplayName() {
        return "Server";
    }

    public void stop(boolean bl) {
        this.running.set(false);
        if (bl) {
            try {
                this.serverThread.join();
            } catch (InterruptedException e) {
                LOGGER.error("Error occurred while shutting down", e);
            }
        }
    }

    public boolean isHost(@Nullable ServerSpider serverSpider) {
        return serverSpider == null || this.getSpiderManager().isHost(serverSpider.getName());
    }

    public synchronized void startGame() {
        if (this.game != null && !this.game.getPlayingSpiders().isEmpty()) {
            return;
        }

        this.game = new SpidersThreadV2Game(this, this.spiderManager.getSpiders());
        this.game.getPlayingSpiders().forEach(spider -> {
            spider.sendPacket(new ChatS2CPacket("もうすぐでゲームが始まります！"));
            new ServerPlayPacketListenerImpl(this, spider.connection.getConnection(), spider);
            spider.sendPacket(new StartGameS2CPacket());
        });
        this.game.start();
    }

    public synchronized void restartGame() {
        if (this.game == null) {
            return;
        }

        this.game = new SpidersThreadV2Game(this, this.game.getPlayingSpiders());
        this.game.getPlayingSpiders().forEach(spider -> {
            spider.sendPacket(new ChatS2CPacket("もうすぐでゲームが始まります！"));
            spider.sendPacket(new RestartGameS2CPacket());
        });
        this.game.start();
    }

    @Nullable
    public SpidersThreadV2Game getGame() {
        return this.game;
    }

    public TopicLoader getTopicLoader() {
        return this.topicLoader;
    }

    public int getCompressionThreshold() {
        return 256;
    }

    public void exit() {
    }

    protected void generateKeyPair() {
        LOGGER.info("Generating keypair");

        try {
            this.keyPair = NetworkEncryptionUtil.generateServerKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate key pair", e);
        }
    }

    public boolean isLoading() {
        return this.loading.get();
    }

    public boolean acceptsStatusQuery() {
        return true;
    }

    public boolean isStopping() {
        return !this.serverThread.isAlive();
    }

    public SpiderManager getSpiderManager() {
        return this.spiderManager;
    }

    public void setSpiderManager(SpiderManager spiderManager) {
        this.spiderManager = spiderManager;
    }

    @Nullable
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
    }

    public int getTicks() {
        return this.ticks;
    }

    @Override
    protected boolean shouldExecuteAsync() {
        return super.shouldExecuteAsync() && !this.isStopped();
    }

    @Override
    public void executeSync(Runnable runnable) {
        if (this.isStopped()) {
            throw new RejectedExecutionException("Server already shutting down");
        } else {
            super.executeSync(runnable);
        }
    }

    @Override
    protected Thread getThread() {
        return this.serverThread;
    }
}
