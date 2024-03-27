package com.hamusuke.threadr.client.network;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.Chat;
import com.hamusuke.threadr.client.gui.component.table.SpiderTable;
import com.hamusuke.threadr.client.gui.window.ConnectingWindow;
import com.hamusuke.threadr.client.gui.window.LoginWindow;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.client.network.main.ClientLobbyPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginKeyC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.login.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.util.function.Consumer;

public class ClientLoginPacketListenerImpl implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ThreadRainbowClient client;
    private final Consumer<String> statusConsumer;
    private final Runnable onJoinLobby;
    private final Connection connection;
    private boolean waitingAuthComplete;
    private int ticks;

    public ClientLoginPacketListenerImpl(Connection connection, ThreadRainbowClient client, Consumer<String> statusConsumer, Runnable onJoinLobby) {
        this.client = client;
        this.connection = connection;
        this.statusConsumer = statusConsumer;
        this.onJoinLobby = onJoinLobby;
    }

    @Override
    public void tick() {
        if (this.waitingAuthComplete && this.ticks % 20 == 0) {
            this.connection.sendPacket(new AliveC2SPacket());
        }

        this.ticks++;
    }

    @Override
    public void handleHello(LoginHelloS2CPacket packet) {
        Cipher cipher;
        Cipher cipher2;
        LoginKeyC2SPacket loginKeyC2SPacket;
        try {
            var secretKey = NetworkEncryptionUtil.generateKey();
            var publicKey = packet.getPublicKey();
            cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, packet.nonce());
        } catch (Exception e) {
            LOGGER.error("Protocol error", e);
            throw new IllegalStateException("Protocol error", e);
        }

        this.statusConsumer.accept("通信を暗号化しています...");
        this.connection.sendPacket(loginKeyC2SPacket, future -> this.connection.setupEncryption(cipher, cipher2));
    }

    @Override
    public void handleSuccess(LoginSuccessS2CPacket packet) {
        this.waitingAuthComplete = false;
        this.statusConsumer.accept("ロビーに参加しています...");
        this.client.clientSpider = new LocalSpider(packet.name());
        this.client.clientSpider.setId(packet.id());
        this.client.spiderTable = new SpiderTable(this.client);
        this.client.addClientSpider(this.client.clientSpider);
        this.client.chat = new Chat(this.client);
        var listener = new ClientLobbyPacketListenerImpl(this.client, this.connection);
        var window = new MainWindow();
        listener.mainWindow = window;
        window.lobby();
        this.client.setCurrentWindow(window);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
        this.onJoinLobby.run();
    }

    @Override
    public void handleDisconnect(LoginDisconnectS2CPacket packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handleCompression(LoginCompressionS2CPacket packet) {
        this.connection.setCompression(packet.threshold(), false);
    }

    @Override
    public void handleEnterName(EnterNameS2CPacket packet) {
        if (!this.waitingAuthComplete) {
            this.waitingAuthComplete = true;
            this.onJoinLobby.run();
        }

        this.client.setCurrentWindow(new LoginWindow(packet.msg()));
    }

    @Override
    public void onDisconnected(String msg) {
        if (this.client.getCurrentWindow() != null) {
            this.client.getCurrentWindow().dispose();
        }

        this.client.setCurrentWindow(new ConnectingWindow(msg));
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
