package com.hamusuke.threadr.network.protocol;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.listener.server.ServerHandshakePacketListener;
import com.hamusuke.threadr.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.ChatC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.DisconnectC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.PingC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.RTTC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.lobby.StartGameC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.LoginKeyC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.SpiderLoginC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.MoveCardC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.*;
import com.hamusuke.threadr.network.protocol.packet.s2c.lobby.StartGameS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.login.*;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.*;
import com.hamusuke.threadr.util.Util;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum Protocol {
    HANDSHAKING(-1, protocol()
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerHandshakePacketListener>()
                    .add(HandshakeC2SPacket.class, HandshakeC2SPacket::new)
            )
    ),
    LOBBY(0, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientLobbyPacketListener>()
                    .add(JoinSpiderS2CPacket.class, JoinSpiderS2CPacket::new)
                    .add(LeaveSpiderS2CPacket.class, LeaveSpiderS2CPacket::new)
                    .add(DisconnectS2CPacket.class, DisconnectS2CPacket::new)
                    .add(ChatS2CPacket.class, ChatS2CPacket::new)
                    .add(PongS2CPacket.class, PongS2CPacket::new)
                    .add(RTTS2CPacket.class, RTTS2CPacket::new)
                    .add(ChangeHostS2CPacket.class, ChangeHostS2CPacket::new)
                    .add(StartGameS2CPacket.class, StartGameS2CPacket::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerLobbyPacketListener>()
                    .add(StartGameC2SPacket.class, StartGameC2SPacket::new)
                    .add(DisconnectC2SPacket.class, DisconnectC2SPacket::new)
                    .add(PingC2SPacket.class, PingC2SPacket::new)
                    .add(RTTC2SPacket.class, RTTC2SPacket::new)
                    .add(ChatC2SPacket.class, ChatC2SPacket::new)
            )
    ),
    PLAY(1, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientPlayPacketListener>()
                    .add(JoinSpiderS2CPacket.class, JoinSpiderS2CPacket::new)
                    .add(LeaveSpiderS2CPacket.class, LeaveSpiderS2CPacket::new)
                    .add(DisconnectS2CPacket.class, DisconnectS2CPacket::new)
                    .add(ChatS2CPacket.class, ChatS2CPacket::new)
                    .add(PongS2CPacket.class, PongS2CPacket::new)
                    .add(RTTS2CPacket.class, RTTS2CPacket::new)
                    .add(ChangeHostS2CPacket.class, ChangeHostS2CPacket::new)
                    .add(GiveLocalCardS2CPacket.class, GiveLocalCardS2CPacket::new)
                    .add(RemoteCardGivenS2CPacket.class, RemoteCardGivenS2CPacket::new)
                    .add(StartTopicSelectionS2CPacket.class, StartTopicSelectionS2CPacket::new)
                    .add(SelectTopicS2CPacket.class, SelectTopicS2CPacket::new)
                    .add(StartMainGameS2CPacket.class, StartMainGameS2CPacket::new)
                    .add(CardMovedS2CPacket.class, CardMovedS2CPacket::new)
                    .add(MainGameFinishedS2CPacket.class, MainGameFinishedS2CPacket::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerPlayPacketListener>()
                    .add(DisconnectC2SPacket.class, DisconnectC2SPacket::new)
                    .add(PingC2SPacket.class, PingC2SPacket::new)
                    .add(RTTC2SPacket.class, RTTC2SPacket::new)
                    .add(ChatC2SPacket.class, ChatC2SPacket::new)
                    .add(ClientCommandC2SPacket.class, ClientCommandC2SPacket::new)
                    .add(MoveCardC2SPacket.class, MoveCardC2SPacket::new)
            )
    ),
    LOGIN(2, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientLoginPacketListener>()
                    .add(LoginDisconnectS2CPacket.class, LoginDisconnectS2CPacket::new)
                    .add(LoginHelloS2CPacket.class, LoginHelloS2CPacket::new)
                    .add(LoginSuccessS2CPacket.class, LoginSuccessS2CPacket::new)
                    .add(LoginCompressionS2CPacket.class, LoginCompressionS2CPacket::new)
                    .add(AliveS2CPacket.class, AliveS2CPacket::new)
                    .add(EnterNameS2CPacket.class, EnterNameS2CPacket::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerLoginPacketListener>()
                    .add(LoginHelloC2SPacket.class, LoginHelloC2SPacket::new)
                    .add(LoginKeyC2SPacket.class, LoginKeyC2SPacket::new)
                    .add(AliveC2SPacket.class, AliveC2SPacket::new)
                    .add(SpiderLoginC2SPacket.class, SpiderLoginC2SPacket::new)
            )
    );

    private static final int MIN = -1;
    private static final int MAX = 2;
    private static final Protocol[] PROTOCOLS = new Protocol[MAX - MIN + 1];

    static {
        for (Protocol protocol : values()) {
            int id = protocol.getStateId();
            if (id < MIN || id > MAX) {
                throw new Error("Invalid protocol ID " + id);
            }

            PROTOCOLS[id - MIN] = protocol;
        }
    }

    private final int stateId;
    private final Map<PacketDirection, ? extends PacketSet<?>> packetHandlers;

    private static Builder protocol() {
        return new Builder();
    }

    Protocol(int stateId, Builder builder) {
        this.stateId = stateId;
        this.packetHandlers = builder.packetHandlers;
    }

    @Nullable
    public static Protocol byId(int id) {
        return id >= MIN && id <= MAX ? PROTOCOLS[id - MIN] : null;
    }

    public Integer getPacketId(PacketDirection direction, Packet<?> packet) {
        return this.packetHandlers.get(direction).getId(packet.getClass());
    }

    public Packet<?> createPacket(PacketDirection direction, int id, IntelligentByteBuf byteBuf) {
        return this.packetHandlers.get(direction).create(id, byteBuf);
    }

    public int getStateId() {
        return this.stateId;
    }

    static class PacketSet<T extends PacketListener> {
        final Object2IntMap<Class<? extends Packet<? super T>>> packetIds = Util.makeAndAccess(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
        private final List<Function<IntelligentByteBuf, ? extends Packet<? super T>>> idToInitializer = Lists.newArrayList();

        public <P extends Packet<? super T>> PacketSet<T> add(Class<P> clazz, Function<IntelligentByteBuf, P> function) {
            int i = this.idToInitializer.size();
            int j = this.packetIds.put(clazz, i);

            if (j != -1) {
                throw new IllegalArgumentException("Packet " + clazz + " is already registered to ID " + j);
            } else {
                this.idToInitializer.add(function);
                return this;
            }
        }

        @Nullable
        public Integer getId(Class<?> clazz) {
            int i = this.packetIds.getInt(clazz);
            return i == -1 ? null : i;
        }

        @Nullable
        public Packet<?> create(int id, IntelligentByteBuf byteBuf) {
            if (0 > id || this.idToInitializer.size() <= id) {
                return null;
            }

            var function = this.idToInitializer.get(id);
            return function != null ? function.apply(byteBuf) : null;
        }

        public Iterable<Class<? extends Packet<? super T>>> getPacketIds() {
            return Iterables.unmodifiableIterable(this.packetIds.keySet());
        }
    }

    static class Builder {
        final Map<PacketDirection, PacketSet<?>> packetHandlers = new EnumMap<>(PacketDirection.class);

        public Builder addDirection(PacketDirection direction, PacketSet<?> packetSet) {
            this.packetHandlers.put(direction, packetSet);
            return this;
        }
    }
}
