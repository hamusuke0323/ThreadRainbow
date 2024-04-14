package com.hamusuke.threadr.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.command.Commands;
import com.hamusuke.threadr.game.topic.TopicLoader;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.server.gui.ThreadRainbowServerGui;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.room.ServerRoom;
import com.hamusuke.threadr.util.Util;
import com.hamusuke.threadr.util.thread.ReentrantThreadExecutor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class ThreadRainbowServer extends ReentrantThreadExecutor<ServerTask> implements AutoCloseable, CommandSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerNetworkIo networkIo;
    private final AtomicBoolean running = new AtomicBoolean();
    private final Thread serverThread;
    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final List<String> commandQueue = Collections.synchronizedList(Lists.newArrayList());
    private final String serverIp;
    private final int serverPort;
    private boolean stopped;
    private int ticks;
    private final SpiderManager spiderManager = new SpiderManager();
    @Nullable
    private KeyPair keyPair;
    private boolean waitingForNextTick;
    private long nextTickTimestamp;
    private long timeReference;
    private long lastTimeReference;
    private final AtomicBoolean loading = new AtomicBoolean();
    private final TopicLoader topicLoader = new TopicLoader();
    private final Map<Integer, ServerRoom> rooms = Maps.newConcurrentMap();
    private final List<Runnable> tickables = Lists.newArrayList();
    @Nullable
    private ThreadRainbowServerGui gui;

    public ThreadRainbowServer(Thread serverThread, String host, int port) {
        super("Server");
        this.running.set(true);
        this.networkIo = new ServerNetworkIo(this);
        this.serverThread = serverThread;
        this.serverIp = host;
        this.serverPort = port;
    }

    public static ThreadRainbowServer startServer(String host, int port, boolean noGui) {
        var atomicReference = new AtomicReference<ThreadRainbowServer>();
        var thread = new Thread(() -> atomicReference.get().runServer(), "Server Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Error occurred in server thread", e));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            thread.setPriority(8);
        }

        var server = new ThreadRainbowServer(thread, host, port);
        if (!noGui && !GraphicsEnvironment.isHeadless()) {
            server.showGui();
        }

        atomicReference.set(server);
        thread.start();
        return server;
    }

    private boolean setupServer() throws IOException {
        var thread = new Thread(this::run, "Server console handler");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        thread.start();
        LOGGER.info("Starting thread rainbow server version {}", Constants.VERSION);
        InetAddress inetAddress = null;
        if (!this.getServerIp().isEmpty()) {
            inetAddress = InetAddress.getByName(this.getServerIp());
        }
        this.generateKeyPair();
        LOGGER.info("Starting thread rainbow server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getServerPort());
        this.getNetworkIo().bind(inetAddress, this.getServerPort());
        LOGGER.info("Done! Type '/stop' to stop the server!");

        return true;
    }

    private void run() {
        var bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        String string;
        try {
            while (!this.isStopped() && this.isRunning() && (string = bufferedReader.readLine()) != null) {
                if (string.startsWith("/")) {
                    this.enqueueCommand(string.substring(1));
                } else {
                    this.sendMessageToAll(string);
                }
            }
        } catch (IOException var4) {
            LOGGER.error("Exception handling console input", var4);
        }
    }

    void runServer() {
        try {
            this.topicLoader.loadTopics();
            Commands.registerCommands(this.dispatcher);
            if (this.setupServer()) {
                this.timeReference = Util.getMeasuringTimeMs();

                while (this.running.get()) {
                    long l = Util.getMeasuringTimeMs() - this.timeReference;
                    if (l > 2000L && this.timeReference - this.lastTimeReference >= 15000L) {
                        long m = l / 50L;
                        LOGGER.warn("The server is too slow! Running {}ms or {} ticks behind", l, m);
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

    private void showGui() {
        if (this.gui == null) {
            this.gui = ThreadRainbowServerGui.showGuiFor(this);
        }
    }

    public synchronized void createRoom(ServerSpider creator, String name, String password) {
        var room = new ServerRoom(this, name, password);
        room.join(creator);
        this.rooms.put(room.getId(), room);
    }

    public synchronized void removeRoom(ServerRoom room) {
        this.rooms.remove(room.getId());
    }

    public Map<Integer, ServerRoom> getRoomMap() {
        return ImmutableMap.copyOf(this.rooms);
    }

    public List<ServerRoom> getRooms() {
        return ImmutableList.copyOf(this.rooms.values());
    }

    public void tick() {
        this.ticks++;
        this.getNetworkIo().tick();
        this.tickables.forEach(Runnable::run);
        this.runQueuedCommands();
    }

    public void addTickable(Runnable runnable) {
        this.tickables.add(runnable);
    }

    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.waitingForNextTick ? this.nextTickTimestamp : this.timeReference);
    }

    private void runTasksTillTickEnd() {
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
            spider.sendError(e.getMessage());
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
            this.spiderManager.sendPacketToAll(new ChatNotify(String.format("[%s] %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public void sendCommandFeedback(String msg, boolean all) {
        LOGGER.info(msg);

        if (all) {
            this.spiderManager.sendPacketToAll(new ChatNotify(String.format("[%s]: %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public String getDisplayName() {
        return "サーバー";
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

    public TopicLoader getTopicLoader() {
        return this.topicLoader;
    }

    public int getCompressionThreshold() {
        return 256;
    }

    public void exit() {
        if (this.gui != null) {
            this.gui.close();
        }
    }

    public void enqueueCommand(String command) {
        this.commandQueue.add(command);
    }

    public void runQueuedCommands() {
        while (!this.commandQueue.isEmpty()) {
            var command = this.commandQueue.remove(0);
            this.runCommand(command);
        }
    }

    private void runCommand(String command) {
        try {
            this.dispatcher.execute(command, this);
        } catch (CommandSyntaxException e) {
            this.sendError(e.getMessage());
        }
    }

    public SpiderManager getSpiderManager() {
        return this.spiderManager;
    }

    private void generateKeyPair() {
        LOGGER.info("Generating keypair");

        try {
            this.keyPair = NetworkEncryptionUtil.generateServerKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate key pair", e);
        }
    }

    @Nullable
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
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
