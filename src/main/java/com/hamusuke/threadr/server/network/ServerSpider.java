package com.hamusuke.threadr.server.network;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.network.listener.server.ServerPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.RTTChangeNotify;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.game.card.ServerCard;
import com.hamusuke.threadr.server.room.ServerRoom;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;

public class ServerSpider extends Spider implements CommandSource {
    public final ThreadRainbowServer server;
    public ServerPacketListener connection;
    private boolean isAuthorized;
    private ServerCard holdingCard;
    @Nullable
    public ServerRoom curRoom;

    public ServerSpider(String name, ThreadRainbowServer server) {
        super(name);
        this.server = server;
    }

    public boolean isHost() {
        if (this.curRoom == null) {
            return false;
        }

        return this.curRoom.isHost(this);
    }

    @Override
    public void setPing(int ping) {
        super.setPing(ping);

        if (this.curRoom != null) {
            this.curRoom.sendPacketToAllInRoom(new RTTChangeNotify(this.getId(), ping));
        }
    }

    public boolean isAuthorized() {
        return this.isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        this.isAuthorized = authorized;
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        this.connection.getConnection().sendPacket(packet, callback);
    }

    public void takeCard(ServerCard card) {
        this.holdingCard = card;
    }

    public ServerCard getHoldingCard() {
        return this.holdingCard;
    }

    @Override
    public ServerSpider getSender() {
        return this;
    }

    @Override
    public ThreadRainbowServer getServer() {
        return this.server;
    }

    @Override
    public void sendMessage(String msg, boolean all) {
        this.sendPacket(new ChatNotify(String.format("<%s> %s", this.getDisplayName(), msg)));

        if (all && this.curRoom != null) {
            this.curRoom.sendPacketToOthersInRoom(this, new ChatNotify(String.format("<%s> %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public void sendCommandFeedback(String msg, boolean all) {
        this.sendPacket(new ChatNotify(msg));

        if (all && this.curRoom != null) {
            this.curRoom.sendPacketToOthersInRoom(this, new ChatNotify(String.format("[%s]: %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }
}
