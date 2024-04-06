package com.hamusuke.threadr.server.network.listener.main;

import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.main.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.RoomListNotify;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.ChatReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.RTTChangeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.CreateRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.JoinRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListQueryReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListReq;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.room.ServerRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class ServerLobbyPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerLobbyPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();

    public ServerLobbyPacketListenerImpl(ThreadRainbowServer server, Connection connection, ServerSpider spider) {
        super(server, connection, spider);
    }

    @Override
    public void handleJoinRoom(JoinRoomReq packet) {
        var room = this.server.getRoomMap().get(packet.id());
        if (room == null) {
            return;
        }

        this.spider.currentRoom = room;
        room.join(this.spider);
    }

    @Override
    public void handleRoomList(RoomListReq packet) {
        var rooms = this.server.getRooms();
        if (rooms.isEmpty()) {
            this.connection.sendPacket(new RoomListNotify(Collections.emptyList()));
            return;
        }

        rooms = rooms.subList(0, Math.min(rooms.size(), 10));
        this.connection.sendPacket(new RoomListNotify(rooms.stream().map(ServerRoom::toInfo).toList()));
    }

    @Override
    public void handleRoomListQuery(RoomListQueryReq packet) {
        var list = this.server.getRooms();
        if (list.isEmpty() || packet.query().isEmpty() || packet.query().isBlank()) {
            this.connection.sendPacket(new RoomListNotify(Collections.emptyList()));
            return;
        }

        list = list.stream().filter(serverRoom -> serverRoom.getRoomName().contains(packet.query())).toList();
        this.connection.sendPacket(new RoomListNotify(list.stream().map(ServerRoom::toInfo).toList()));
    }

    @Override
    public void handleCreateRoom(CreateRoomReq packet) {
        this.server.createRoom(this.spider, packet.roomName(), packet.password());
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.connection.getAddress());
        this.server.getSpiderManager().removeSpider(this.spider);
    }

    @Override
    public void handleChatPacket(ChatReq packet) {
    }

    @Override
    public void handleRTTPacket(RTTChangeReq packet) {
        this.spider.setPing(packet.rtt());
    }
}
