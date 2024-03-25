package com.hamusuke.threadr.network.channel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hamusuke.threadr.network.encryption.PacketDecryptor;
import com.hamusuke.threadr.network.encryption.PacketEncryptor;
import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.protocol.PacketDirection;
import com.hamusuke.threadr.network.protocol.Protocol;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.DisconnectS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.login.LoginDisconnectS2CPacket;
import com.hamusuke.threadr.util.Lazy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
    public static final AttributeKey<Protocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
    public static final Lazy<NioEventLoopGroup> NIO_EVENT_LOOP_GROUP = new Lazy<>(() -> {
        return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    });
    public static final Lazy<EpollEventLoopGroup> EPOLL_EVENT_LOOP_GROUP = new Lazy<>(() -> {
        return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile PacketListener packetListener;
    private Channel channel;
    private SocketAddress address;
    private final PacketDirection receiving;
    private final Queue<QueuedPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private boolean disconnected;

    public Connection(PacketDirection receiving) {
        this.receiving = receiving;
    }

    public static Connection connect(InetSocketAddress address) {
        final var connection = new Connection(PacketDirection.CLIENTBOUND);
        Class<? extends SocketChannel> clazz;
        Lazy<? extends EventLoopGroup> lazy;
        if (Epoll.isAvailable()) {
            clazz = EpollSocketChannel.class;
            lazy = EPOLL_EVENT_LOOP_GROUP;
        } else {
            clazz = NioSocketChannel.class;
            lazy = NIO_EVENT_LOOP_GROUP;
        }

        new Bootstrap().group(lazy.get()).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }

                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(PacketDirection.CLIENTBOUND)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(PacketDirection.SERVERBOUND)).addLast(new FlowControlHandler()).addLast("packet_handler", connection);
            }
        }).channel(clazz).connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        return connection;
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void handle(Packet<T> packet, PacketListener packetListener) {
        try {
            packet.handle((T) packetListener);
        } catch (Exception e) {
            if (packetListener.shouldCrashOnException()) {
                throw new RuntimeException(e);
            }

            LOGGER.warn("Error occurred while handling packet", e);
        }
    }

    public void setupEncryption(Cipher decryptionCipher, Cipher encryptionCipher) {
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(decryptionCipher));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(encryptionCipher));
        LOGGER.debug("The connection has been encrypted");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.disconnect();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();

        this.setProtocol(Protocol.HANDSHAKING);
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (this.channel.isOpen()) {
            LOGGER.warn(String.format("Caught exception in %s side", this.receiving == PacketDirection.SERVERBOUND ? "server" : "client"), cause);
            this.sendPacket(this.getProtocol() == Protocol.LOGIN ? new LoginDisconnectS2CPacket() : new DisconnectS2CPacket(), future -> {
                this.disconnect();
            });
            this.disableAutoRead();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) {
        if (this.isConnected()) {
            handle(msg, this.packetListener);
        }
    }

    public void tick() {
        this.sendQueuedPackets();

        if (this.packetListener != null) {
            this.packetListener.tick();
        }

        if (!this.isConnected() && !this.disconnected) {
            this.handleDisconnection();
        }

        if (this.channel != null) {
            this.channel.flush();
        }
    }

    public void setListener(PacketListener listener) {
        Validate.notNull(listener, "packetListener");
        this.packetListener = listener;
    }

    public void setCompression(int threshold, boolean validate) {
        var decompress = this.channel.pipeline().get("decompress");
        var compress = this.channel.pipeline().get("compress");

        if (threshold >= 0) {
            if (decompress instanceof PacketInflater inflater) {
                inflater.setThreshold(threshold, validate);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(threshold, validate));
            }

            if (compress instanceof PacketDeflater deflater) {
                deflater.setThreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(threshold));
            }
        } else {
            if (decompress instanceof PacketInflater) {
                this.channel.pipeline().remove("decompress");
            }

            if (compress instanceof PacketDeflater) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        if (this.isConnected()) {
            this.sendQueuedPackets();
            this.sendImmediately(packet, callback);
        } else {
            this.packetQueue.add(new QueuedPacket(packet, callback));
        }
    }

    private void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        if (this.channel.eventLoop().inEventLoop()) {
            this.sendInternal(packet, callback);
        } else {
            this.channel.eventLoop().execute(() -> this.sendInternal(packet, callback));
        }
    }

    private void sendInternal(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        var channelFuture = this.channel.writeAndFlush(packet);
        if (callback != null) {
            channelFuture.addListener(callback);
        }

        if (packet.nextProtocol() != null) {
            this.disableAutoRead();
            channelFuture.addListener(future -> this.setProtocol(packet.nextProtocol()));
        }
        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private Protocol getProtocol() {
        return this.channel.attr(ATTRIBUTE_PROTOCOL).get();
    }

    public void setProtocol(Protocol state) {
        this.channel.attr(ATTRIBUTE_PROTOCOL).set(state);
        LOGGER.debug("Connection: {}, Protocol changed to '{}'", this.getAddress(), state);
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    private void sendQueuedPackets() {
        if (this.channel != null && this.channel.isOpen()) {
            synchronized (this.packetQueue) {
                QueuedPacket queuedPacket;
                while ((queuedPacket = this.packetQueue.poll()) != null) {
                    this.sendImmediately(queuedPacket.packet, queuedPacket.callback);
                }
            }
        }
    }

    public void disconnect() {
        if (this.isConnected()) {
            this.channel.close().awaitUninterruptibly();
        }
    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.disconnected) {
                LOGGER.warn("handleDisconnection() called twice");
            } else {
                this.disconnected = true;
                this.getPacketListener().onDisconnected();
            }
        }
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    record QueuedPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
    }
}
