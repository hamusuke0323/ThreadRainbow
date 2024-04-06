package com.hamusuke.threadr.network.protocol;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.threadr.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.threadr.network.listener.client.main.ClientLobbyPacketListener;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.threadr.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.threadr.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.threadr.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.*;
import com.hamusuke.threadr.network.protocol.packet.clientbound.info.ServerInfoRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.RoomListNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.*;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.StartGameNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.ChatReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.DisconnectReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.PingReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.RTTChangeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.handshake.HandshakeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.info.ServerInfoReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.CreateRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.JoinRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListQueryReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.AliveReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EncryptionSetupReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EnterNameRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.KeyExchangeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.StartGameReq;
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
                    .add(HandshakeReq.class, HandshakeReq::new)
            )
    ),
    LOBBY(0, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientLobbyPacketListener>()
                    .add(SpiderJoinNotify.class, SpiderJoinNotify::new)
                    .add(SpiderLeaveNotify.class, SpiderLeaveNotify::new)
                    .add(DisconnectNotify.class, DisconnectNotify::new)
                    .add(ChatNotify.class, ChatNotify::new)
                    .add(PongRsp.class, PongRsp::new)
                    .add(RTTChangeNotify.class, RTTChangeNotify::new)
                    .add(ChangeHostNotify.class, ChangeHostNotify::new)
                    .add(RoomListNotify.class, RoomListNotify::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerLobbyPacketListener>()
                    .add(DisconnectReq.class, DisconnectReq::new)
                    .add(PingReq.class, PingReq::new)
                    .add(RTTChangeReq.class, RTTChangeReq::new)
                    .add(ChatReq.class, ChatReq::new)
                    .add(CreateRoomReq.class, CreateRoomReq::new)
                    .add(JoinRoomReq.class, JoinRoomReq::new)
                    .add(RoomListQueryReq.class, RoomListQueryReq::new)
                    .add(RoomListReq.class, RoomListReq::new)
            )
    ),
    ROOM(1, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientRoomPacketListener>()
                    .add(SpiderJoinNotify.class, SpiderJoinNotify::new)
                    .add(SpiderLeaveNotify.class, SpiderLeaveNotify::new)
                    .add(DisconnectNotify.class, DisconnectNotify::new)
                    .add(ChatNotify.class, ChatNotify::new)
                    .add(PongRsp.class, PongRsp::new)
                    .add(RTTChangeNotify.class, RTTChangeNotify::new)
                    .add(ChangeHostNotify.class, ChangeHostNotify::new)
                    .add(StartGameNotify.class, StartGameNotify::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerRoomPacketListener>()
                    .add(DisconnectReq.class, DisconnectReq::new)
                    .add(PingReq.class, PingReq::new)
                    .add(RTTChangeReq.class, RTTChangeReq::new)
                    .add(ChatReq.class, ChatReq::new)
                    .add(StartGameReq.class, StartGameReq::new)
            )
    ),
    PLAY(2, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientPlayPacketListener>()
                    .add(SpiderJoinNotify.class, SpiderJoinNotify::new)
                    .add(SpiderLeaveNotify.class, SpiderLeaveNotify::new)
                    .add(DisconnectNotify.class, DisconnectNotify::new)
                    .add(ChatNotify.class, ChatNotify::new)
                    .add(PongRsp.class, PongRsp::new)
                    .add(RTTChangeNotify.class, RTTChangeNotify::new)
                    .add(ChangeHostNotify.class, ChangeHostNotify::new)
                    .add(LocalCardHandedNotify.class, LocalCardHandedNotify::new)
                    .add(RemoteCardGivenNotify.class, RemoteCardGivenNotify::new)
                    .add(StartTopicSelectionNotify.class, StartTopicSelectionNotify::new)
                    .add(TopicChangeNotify.class, TopicChangeNotify::new)
                    .add(StartMainGameNotify.class, StartMainGameNotify::new)
                    .add(CardMoveNotify.class, CardMoveNotify::new)
                    .add(FinishMainGameNotify.class, FinishMainGameNotify::new)
                    .add(UncoverCardNotify.class, UncoverCardNotify::new)
                    .add(RestartGameNotify.class, RestartGameNotify::new)
                    .add(ExitGameNotify.class, ExitGameNotify::new)
                    .add(SpiderExitGameNotify.class, SpiderExitGameNotify::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerPlayPacketListener>()
                    .add(DisconnectReq.class, DisconnectReq::new)
                    .add(PingReq.class, PingReq::new)
                    .add(RTTChangeReq.class, RTTChangeReq::new)
                    .add(ChatReq.class, ChatReq::new)
                    .add(ClientCommandReq.class, ClientCommandReq::new)
                    .add(MoveCardReq.class, MoveCardReq::new)
            )
    ),
    LOGIN(3, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientLoginPacketListener>()
                    .add(LoginDisconnectNotify.class, LoginDisconnectNotify::new)
                    .add(KeyExchangeRsp.class, KeyExchangeRsp::new)
                    .add(LoginSuccessNotify.class, LoginSuccessNotify::new)
                    .add(LoginCompressionNotify.class, LoginCompressionNotify::new)
                    .add(AliveRsp.class, AliveRsp::new)
                    .add(EnterNameReq.class, EnterNameReq::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerLoginPacketListener>()
                    .add(KeyExchangeReq.class, KeyExchangeReq::new)
                    .add(EncryptionSetupReq.class, EncryptionSetupReq::new)
                    .add(AliveReq.class, AliveReq::new)
                    .add(EnterNameRsp.class, EnterNameRsp::new)
            )
    ),
    INFO(4, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientInfoPacketListener>()
                    .add(ServerInfoRsp.class, ServerInfoRsp::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerInfoPacketListener>()
                    .add(ServerInfoReq.class, ServerInfoReq::new)
            )
    );

    private static final int MIN = -1;
    private static final int MAX = 4;
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
    }

    static class Builder {
        final Map<PacketDirection, PacketSet<?>> packetHandlers = new EnumMap<>(PacketDirection.class);

        public Builder addDirection(PacketDirection direction, PacketSet<?> packetSet) {
            this.packetHandlers.put(direction, packetSet);
            return this;
        }
    }
}
