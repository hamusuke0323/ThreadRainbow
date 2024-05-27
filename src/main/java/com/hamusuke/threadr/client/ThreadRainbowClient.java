package com.hamusuke.threadr.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.MainWindow;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.client.gui.component.panel.ServerListPanel;
import com.hamusuke.threadr.client.gui.component.table.PacketLogTable;
import com.hamusuke.threadr.client.gui.component.table.SpiderTable;
import com.hamusuke.threadr.client.network.Chat;
import com.hamusuke.threadr.client.network.listener.info.ClientInfoPacketListenerImpl;
import com.hamusuke.threadr.client.network.listener.login.ClientLoginPacketListenerImpl;
import com.hamusuke.threadr.client.network.listener.main.ClientCommonPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.AbstractClientSpider;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.network.ServerInfo;
import com.hamusuke.threadr.network.ServerInfo.Status;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.PongRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.RTTChangeNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.LobbyPongRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.AliveRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.DisconnectReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.PingReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.RTTChangeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.handshake.HandshakeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.info.ServerInfoReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.LobbyDisconnectReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.LobbyPingReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.AliveReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.KeyExchangeReq;
import com.hamusuke.threadr.room.RoomInfo;
import com.hamusuke.threadr.util.Util;
import com.hamusuke.threadr.util.thread.ReentrantThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ThreadRainbowClient extends ReentrantThreadExecutor<Runnable> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ThreadRainbowClient INSTANCE;
    private static final Gson GSON = new Gson();
    private final AtomicBoolean running = new AtomicBoolean();
    private final TickCounter tickCounter = new TickCounter(20.0F, 0L);
    @Nullable
    private Connection connection;
    @Nullable
    public ClientCommonPacketListenerImpl listener;
    private final MainWindow mainWindow;
    @Nullable
    public DefaultListModel<NumberCard> model;
    @Nullable
    public LocalSpider clientSpider;
    private Thread thread;
    private int tickCount;
    public final List<AbstractClientSpider> clientSpiders = Lists.newArrayList();
    public SpiderTable spiderTable;
    public Chat chat;
    @Nullable
    public RoomInfo curRoom;
    public final PacketLogTable packetLogTable = new PacketLogTable();
    private final List<String> packetFilters = Collections.synchronizedList(Lists.newArrayList(
            AliveReq.class.getSimpleName(),
            AliveRsp.class.getSimpleName(),
            LobbyPingReq.class.getSimpleName(),
            LobbyPongRsp.class.getSimpleName(),
            PingReq.class.getSimpleName(),
            PongRsp.class.getSimpleName(),
            RTTChangeReq.class.getSimpleName(),
            RTTChangeNotify.class.getSimpleName()
    ));
    private final File serversFile;
    private final List<ServerInfo> servers = Collections.synchronizedList(Lists.newArrayList());
    private final List<Connection> infoConnections = Collections.synchronizedList(Lists.newArrayList());

    ThreadRainbowClient() {
        super("Client");

        if (INSTANCE != null) {
            throw new IllegalStateException("ThreadRainbowClient is singleton class!");
        }

        INSTANCE = this;
        this.running.set(true);
        this.thread = Thread.currentThread();
        this.serversFile = new File("./servers.json");
        this.loadServers();

        this.mainWindow = new MainWindow(this);
        this.mainWindow.setPanel(new ServerListPanel());
        this.mainWindow.setSize(1280, 720);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setVisible(true);
    }

    public static ThreadRainbowClient getInstance() {
        return INSTANCE;
    }

    private synchronized void loadServers() {
        this.servers.clear();

        try {
            if (!this.serversFile.exists() || !this.serversFile.isFile()) {
                this.serversFile.createNewFile();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load servers", e);
            return;
        }

        if (this.serversFile.exists() && this.serversFile.isFile()) {
            try (var isr = new InputStreamReader(new FileInputStream(this.serversFile), StandardCharsets.UTF_8)) {
                var servers = GSON.fromJson(isr, JsonArray.class);
                if (servers == null) {
                    return;
                }

                Set<ServerInfo> set = Sets.newHashSet();
                servers.forEach(e -> {
                    try {
                        var info = GSON.fromJson(e, ServerInfo.class);
                        if (info == null) {
                            return;
                        }

                        info.status = Status.NONE;
                        set.add(info);
                    } catch (Exception ex) {
                        LOGGER.warn("Error occurred while loading json and skip", ex);
                    }
                });

                this.servers.addAll(set);
            } catch (Exception e) {
                LOGGER.warn("Failed to load servers json file", e);
            }
        }
    }

    public synchronized void saveServers() {
        try (var w = new OutputStreamWriter(new FileOutputStream(this.serversFile), StandardCharsets.UTF_8)) {
            GSON.toJson(this.servers, w);
            w.flush();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving servers", e);
        }
    }

    public List<ServerInfo> getServers() {
        return ImmutableList.copyOf(this.servers);
    }

    public boolean addServer(ServerInfo info) {
        if (this.servers.contains(info)) {
            return false;
        }

        this.servers.add(info);
        return true;
    }

    public void removeServer(ServerInfo info) {
        this.servers.remove(info);
    }

    public void checkServerInfo(ServerInfo info) {
        CompletableFuture.runAsync(() -> {
            info.status = Status.CONNECTING;
            this.onServerInfoChanged();
            var address = new InetSocketAddress(info.address, info.port);
            var connection = Connection.connect(this, address);
            connection.setListener(new ClientInfoPacketListenerImpl(this, connection, info));
            connection.sendPacket(new HandshakeReq(Protocol.INFO));
            this.infoConnections.add(connection);
        }, this).exceptionally(throwable -> {
            info.status = Status.FAILED;
            this.onServerInfoChanged();
            return null;
        });
    }

    public void onServerInfoChanged() {
        if (this.getPanel() instanceof ServerListPanel panel) {
            panel.onServerInfoChanged();
        }
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
        } finally {
            this.stop();
        }
    }

    public String getGameTitle() {
        return "Thread Rainbow " + Constants.VERSION;
    }

    public void setWindowTitle(String title) {
        this.mainWindow.setTitle(title);
    }

    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    public Panel getPanel() {
        return this.mainWindow.getPanel();
    }

    public void setPanel(Panel panel) {
        this.mainWindow.setPanel(panel);
    }

    public void stopLooping() {
        if (this.connection != null) {
            this.connection.disconnect("Client Exit");
        }

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

        SwingUtilities.invokeLater(this.mainWindow::tick);
        if (this.connection != null) {
            this.connection.tick();
            if (this.connection.isDisconnected()) {
                this.connection = null;
            }
        }

        this.infoConnections.forEach(Connection::tick);
        this.infoConnections.removeIf(Connection::isDisconnected);
    }

    public void addClientSpider(AbstractClientSpider clientSpider) {
        synchronized (this.clientSpiders) {
            this.clientSpiders.add(clientSpider);
        }
    }

    public void connectToServer(String host, int port, Consumer<String> consumer) {
        this.clientSpider = null;
        var address = new InetSocketAddress(host, port);
        this.connection = Connection.connect(this, address);
        this.connection.setListener(new ClientLoginPacketListenerImpl(this.connection, this, consumer));
        this.connection.sendPacket(new HandshakeReq(Protocol.LOGIN));
        this.connection.sendPacket(new KeyExchangeReq());
    }

    public boolean amIHost() {
        return this.listener != null && this.clientSpider != null && this.listener.getHostId() == this.clientSpider.getId();
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

        if (this.connection.getPacketListener() instanceof ClientLobbyPacketListener) {
            this.connection.sendPacket(new LobbyDisconnectReq(), future -> this.connection.disconnect(""));
            return;
        }

        this.connection.sendPacket(new DisconnectReq(), future -> this.connection.disconnect(""));
    }
}
