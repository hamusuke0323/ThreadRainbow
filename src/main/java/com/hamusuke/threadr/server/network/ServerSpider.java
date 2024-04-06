package com.hamusuke.threadr.server.network;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.game.card.ServerCard;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.listener.main.ServerCommonPacketListenerImpl;
import com.hamusuke.threadr.server.room.ServerRoom;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;

public class ServerSpider extends Spider implements CommandSource {
    public final ThreadRainbowServer server;
    public ServerCommonPacketListenerImpl connection;
    private boolean isAuthorized;
    private ServerCard holdingCard;
    @Nullable
    public ServerRoom currentRoom;

    public ServerSpider(String name, ThreadRainbowServer server) {
        super(name);
        this.server = server;
    }

    public boolean isHost() {
        if (this.currentRoom == null) {
            return false;
        }

        return this.currentRoom.isHost(this);
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

    public void sendPacketToOthers(Packet<?> packet) {
        this.sendPacketToOthers(packet, null);
    }

    public void sendPacketToOthers(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        this.server.getSpiderManager().sendPacketToOthers(this, packet, callback);
    }

    public void takeCard(ServerCard card) {
        this.holdingCard = card;
    }

    public ServerCard getHoldingCard() {
        return this.holdingCard;
    }

    @Nullable
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

        if (all) {
            this.sendPacketToOthers(new ChatNotify(String.format("<%s> %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public void sendCommandFeedback(String msg, boolean all) {
        this.sendPacket(new ChatNotify(msg));

        if (all) {
            this.sendPacketToOthers(new ChatNotify(String.format("[%s]: %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }
}
