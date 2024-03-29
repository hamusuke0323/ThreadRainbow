package com.hamusuke.threadr.client;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.client.gui.component.Chat;
import com.hamusuke.threadr.client.gui.component.table.PacketLogTable;
import com.hamusuke.threadr.client.gui.component.table.SpiderTable;
import com.hamusuke.threadr.client.gui.window.ConnectingWindow;
import com.hamusuke.threadr.client.gui.window.Window;
import com.hamusuke.threadr.client.network.listener.login.ClientLoginPacketListenerImpl;
import com.hamusuke.threadr.client.network.listener.main.ClientCommonPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.AbstractClientSpider;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.DisconnectC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.PingC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.RTTC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.PongS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.RTTS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.login.AliveS2CPacket;
import com.hamusuke.threadr.util.Util;
import com.hamusuke.threadr.util.thread.ReentrantThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ThreadRainbowClient extends ReentrantThreadExecutor<Runnable> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ThreadRainbowClient INSTANCE;
    private final AtomicBoolean running = new AtomicBoolean();
    private final TickCounter tickCounter = new TickCounter(20.0F, 0L);
    @Nullable
    private Connection connection;
    @Nullable
    public ClientCommonPacketListenerImpl listener;
    @Nullable
    public LocalSpider clientSpider;
    @Nullable
    private Window currentWindow;
    private Thread thread;
    private int tickCount;
    public final List<AbstractClientSpider> clientSpiders = Lists.newArrayList();
    public SpiderTable spiderTable;
    public Chat chat;
    public final PacketLogTable packetLogTable = new PacketLogTable();
    private final List<String> packetFilters = Collections.synchronizedList(Lists.newArrayList(
            AliveC2SPacket.class.getSimpleName(),
            AliveS2CPacket.class.getSimpleName(),
            PingC2SPacket.class.getSimpleName(),
            PongS2CPacket.class.getSimpleName(),
            RTTC2SPacket.class.getSimpleName(),
            RTTS2CPacket.class.getSimpleName()
    ));

    ThreadRainbowClient() {
        super("Client");

        if (INSTANCE != null) {
            throw new IllegalStateException("ThreadRainbowClient can be instantiated just once!");
        }

        INSTANCE = this;
        this.running.set(true);
        this.thread = Thread.currentThread();
        this.setCurrentWindow(new ConnectingWindow());
    }

    public static ThreadRainbowClient getInstance() {
        return INSTANCE;
    }

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }

        try {
            boolean bl = false;

            while (this.running.get()) {
                try {
                    this.loop(!bl);
                } catch (OutOfMemoryError e) {
                    if (bl) {
                        throw e;
                    }

                    System.gc();
                    LOGGER.fatal("Out of memory", e);
                    bl = true;
                }
            }
        } catch (Exception e) {
            LOGGER.fatal("Error thrown!", e);
        }
    }

    public String getAddresses() {
        return this.connection == null ? "" : String.format("Client Address: %s, Server Address: %s", this.connection.getChannel().localAddress(), this.connection.getChannel().remoteAddress());
    }

    @Nullable
    public Window getCurrentWindow() {
        return this.currentWindow;
    }

    public void setCurrentWindow(@Nullable Window currentWindow) {
        this.currentWindow = currentWindow;

        if (this.currentWindow == null) {
            this.currentWindow = new ConnectingWindow();
        }

        this.currentWindow.init();
        this.currentWindow.setVisible(true);
    }

    public void stopLooping() {
        this.running.set(false);
    }

    public void stop() {
        try {
            LOGGER.info("Stopping");
            this.close();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while stopping", e);
        }
    }

    private void loop(boolean tick) {
        if (tick) {
            int i = this.tickCounter.beginLoopTick(Util.getMeasuringTimeMs());
            this.runTasks();
            for (int j = 0; j < Math.min(10, i); j++) {
                this.tick();
            }
        }
    }

    public void tick() {
        this.tickCount++;

        if (this.currentWindow != null) {
            this.currentWindow.tick();
        }

        if (this.connection != null) {
            this.connection.tick();
            if (this.connection.isDisconnected()) {
                this.connection = null;
            }
        }
    }

    public void addClientSpider(AbstractClientSpider clientSpider) {
        synchronized (this.clientSpiders) {
            this.clientSpiders.add(clientSpider);
        }
    }

    public void connectToServer(String host, int port, Consumer<String> consumer, Runnable onJoinLobby) {
        this.clientSpider = null;
        InetSocketAddress address = new InetSocketAddress(host, port);
        this.connection = Connection.connect(this, address);
        this.connection.setListener(new ClientLoginPacketListenerImpl(this.connection, this, consumer, onJoinLobby));
        this.connection.sendPacket(new HandshakeC2SPacket(Protocol.LOGIN));
        this.connection.sendPacket(new LoginHelloC2SPacket());
    }

    public void addPacketFilter(String packetName) {
        this.packetFilters.add(packetName);
    }

    public boolean isPacketTrash(Packet<?> packet) {
        return this.packetFilters.contains(packet.getClass().getSimpleName());
    }

    @Override
    public void close() {
        System.exit(0);
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return true;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    @Nullable
    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect() {
        if (this.connection == null) {
            return;
        }

        this.connection.sendPacket(new DisconnectC2SPacket(), future -> this.connection.disconnect(""));
    }
}
