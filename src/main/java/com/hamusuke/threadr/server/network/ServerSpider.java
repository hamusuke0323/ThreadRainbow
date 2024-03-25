package com.hamusuke.threadr.server.network;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.s2c.common.ChatS2CPacket;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.hamusuke.threadr.server.network.main.ServerCommonPacketListenerImpl;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;

public class ServerSpider extends Spider implements CommandSource {
    public final ThreadRainbowServer server;
    public ServerCommonPacketListenerImpl connection;
    private boolean isAuthorized;
    private NumberCard holdingCard;

    public ServerSpider(String name, ThreadRainbowServer server) {
        super(name);
        this.server = server;
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

    public void takeCard(NumberCard card) {
        this.holdingCard = card;
    }

    public NumberCard getHoldingCard() {
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
        this.sendPacket(new ChatS2CPacket(String.format("<%s> %s", this.getDisplayName(), msg)));

        if (all) {
            this.sendPacketToOthers(new ChatS2CPacket(String.format("<%s> %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public void sendCommandFeedback(String msg, boolean all) {
        this.sendPacket(new ChatS2CPacket(msg));

        if (all) {
            this.sendPacketToOthers(new ChatS2CPacket(String.format("[%s]: %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }
}
