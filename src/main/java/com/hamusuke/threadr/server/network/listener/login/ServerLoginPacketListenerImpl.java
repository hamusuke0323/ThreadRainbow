package com.hamusuke.threadr.server.network.listener.login;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.*;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.AliveReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EncryptionSetupReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EnterNameRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.KeyExchangeReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.lobby.ServerLobbyPacketListenerImpl;
import com.mojang.brigadier.StringReader;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIMEOUT_TICKS = 600;
    private static final int ENCRYPTION_WAIT_TICKS = 60;
    private static final Random RANDOM = new Random();
    public final Connection connection;
    final ThreadRainbowServer server;
    private final byte[] nonce = new byte[4];
    State state;
    private int ticks;
    private int encWaitTicks;
    private ServerSpider serverSpider;

    public ServerLoginPacketListenerImpl(ThreadRainbowServer server, Connection connection) {
        this.state = State.KEY_EX;
        this.server = server;
        this.connection = connection;
        RANDOM.nextBytes(this.nonce);
    }

    public static boolean isValidName(String name) {
        return name.chars().filter(value -> !StringReader.isAllowedInUnquotedString((char) value)).findAny().isEmpty();
    }

    @Override
    public void tick() {
        if (this.state == State.ENTER_NAME && this.encWaitTicks > 0) {
            this.encWaitTicks--;
            if (this.encWaitTicks <= 0) {
                this.connection.sendPacket(new EnterNameReq());
            }
        } else if (this.state == State.READY) {
            this.acceptSpider();
        }

        this.ticks++;
        if ((this.state == State.KEY_EX || this.state == State.ENCRYPTION) && this.ticks == TIMEOUT_TICKS) {
            LOGGER.info("Login is too slow");
            this.disconnect("Login is too slow");
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    private void disconnect() {
        this.disconnect("");
    }

    private void disconnect(String msg) {
        try {
            LOGGER.info("Disconnecting {}", this.getConnectionInfo());
            this.connection.sendPacket(new LoginDisconnectNotify(msg));
            this.connection.disconnect(msg);
        } catch (Exception e) {
            LOGGER.error("Error while disconnecting spider", e);
        }
    }

    public void acceptSpider() {
        this.state = State.ACCEPTED;
        if (this.server.getCompressionThreshold() >= 0) {
            this.connection.sendPacket(new LoginCompressionNotify(this.server.getCompressionThreshold()), future -> {
                this.connection.setCompression(this.server.getCompressionThreshold(), true);
            });
        }

        if (this.server.getSpiderManager().canJoin(this.serverSpider)) {
            this.connection.sendPacket(new LoginSuccessNotify(this.serverSpider));
            new ServerLobbyPacketListenerImpl(this.server, this.connection, this.serverSpider);
            this.server.getSpiderManager().addSpider(this.serverSpider);
        } else {
            this.disconnect();
        }
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.getConnectionInfo());
    }

    public String getConnectionInfo() {
        return String.valueOf(this.connection.getAddress());
    }

    @Override
    public void handleKeyEx(KeyExchangeReq packet) {
        Validate.validState(this.state == State.KEY_EX, "Unexpected key exchange packet");
        if (this.state != State.KEY_EX) {
            this.disconnect();
            return;
        }

        if (this.server.getKeyPair() == null) {
            this.disconnect("Internal server error");
            return;
        }

        this.state = State.ENCRYPTION;
        this.connection.sendPacket(new KeyExchangeRsp(this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
    }

    @Override
    public void handleEncryption(EncryptionSetupReq packet) {
        Validate.validState(this.state == State.ENCRYPTION, "Unexpected encryption packet");

        if (this.server.getKeyPair() == null) {
            this.disconnect("Internal server error");
            return;
        }

        var privateKey = this.server.getKeyPair().getPrivate();

        try {
            if (!Arrays.equals(this.nonce, packet.decryptNonce(privateKey))) {
                throw new IllegalStateException("Protocol error");
            }

            var secretKey = packet.decryptSecretKey(privateKey);
            var cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            var cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            this.connection.setupEncryption(cipher, cipher2);
            this.state = State.ENTER_NAME;
            this.encWaitTicks = ENCRYPTION_WAIT_TICKS;
        } catch (Exception e) {
            throw new IllegalStateException("Protocol error", e);
        }
    }

    @Override
    public void handlePing(AliveReq packet) {
        this.connection.sendPacket(new AliveRsp());
    }

    @Override
    public void handleEnterName(EnterNameRsp packet) {
        Validate.validState(this.state == State.ENTER_NAME, "Unexpected enter name packet");

        var res = this.tryLogin(packet.name());
        switch (res) {
            case OK -> {
                this.serverSpider = new ServerSpider(packet.name(), this.server);
                this.serverSpider.setAuthorized(true);
                this.state = State.READY;
            }
            case DUPLICATED_NAME, INVALID_CHARS_IN_NAME ->
                    this.connection.sendPacket(new EnterNameReq(res.messageFactory.apply(packet.name())));
        }
    }

    private LoginResult tryLogin(String name) {
        if (!isValidName(name)) {
            return LoginResult.INVALID_CHARS_IN_NAME;
        }

        if (this.server.getSpiderManager().getSpiders().stream().anyMatch(spider -> spider.getName().equals(name))) {
            return LoginResult.DUPLICATED_NAME;
        }

        return LoginResult.OK;
    }

    private enum State {
        KEY_EX,
        ENCRYPTION,
        ENTER_NAME,
        READY,
        ACCEPTED
    }

    private enum LoginResult {
        OK(Function.identity()),
        INVALID_CHARS_IN_NAME(s -> "使用不可能な文字が含まれています"),
        DUPLICATED_NAME(s -> String.format("'%s' という名前は既に使われています。別の名前を使用してください", s));

        private final Function<String, String> messageFactory;

        LoginResult(Function<String, String> messageFactory) {
            this.messageFactory = messageFactory;
        }
    }
}
