package com.hamusuke.threadr.client.network.listener.login;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel.PanelState;
import com.hamusuke.threadr.client.gui.component.table.SpiderTable;
import com.hamusuke.threadr.client.gui.window.LoginWindow;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.client.gui.window.ServerListWindow;
import com.hamusuke.threadr.client.network.Chat;
import com.hamusuke.threadr.client.network.listener.main.ClientLobbyPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.*;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.AliveReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EncryptionSetupReq;
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
            this.connection.sendPacket(new AliveReq());
        }

        this.ticks++;
    }

    @Override
    public void handleKeyEx(KeyExchangeRsp packet) {
        Cipher cipher;
        Cipher cipher2;
        EncryptionSetupReq req;
        try {
            var secretKey = NetworkEncryptionUtil.generateKey();
            var publicKey = packet.getPublicKey();
            cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            req = new EncryptionSetupReq(secretKey, publicKey, packet.nonce());
        } catch (Exception e) {
            LOGGER.error("Protocol error", e);
            throw new IllegalStateException("Protocol error", e);
        }

        this.statusConsumer.accept("通信を暗号化しています...");
        this.connection.sendPacket(req, future -> this.connection.setupEncryption(cipher, cipher2));
    }

    @Override
    public void handleSuccess(LoginSuccessNotify packet) {
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
        window.changeState(PanelState.LOBBY);
        this.client.setCurrentWindow(window);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
        this.onJoinLobby.run();
    }

    @Override
    public void handleDisconnect(LoginDisconnectNotify packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handleCompression(LoginCompressionNotify packet) {
        this.connection.setCompression(packet.threshold(), false);
    }

    @Override
    public void handleEnterName(EnterNameReq packet) {
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

        this.client.setCurrentWindow(new ServerListWindow(msg));
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
