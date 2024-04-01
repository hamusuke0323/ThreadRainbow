package com.hamusuke.threadr.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.SpiderJoinNotify;
import com.hamusuke.threadr.server.network.ServerSpider;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import java.util.List;

public class SpiderManager {
    private final List<ServerSpider> spiders = Lists.newArrayList();
    @Nullable
    private ServerSpider host;

    public boolean canJoin(ServerSpider serverSpider) {
        return serverSpider.isAuthorized();
    }

    public void addSpider(ServerSpider serverSpider) {
        this.sendPacketToAll(new SpiderJoinNotify(serverSpider));

        synchronized (this.spiders) {
            this.spiders.forEach(serverSpider1 -> serverSpider.sendPacket(new SpiderJoinNotify(serverSpider1)));
            this.spiders.add(serverSpider);
            if (this.host == null) {
                this.host = serverSpider;
            }
        }

        this.sendPacketToAll(new ChatNotify(String.format("%s[%s] がサーバーに参加しました", serverSpider.getName(), serverSpider.connection.getConnection().getAddress())));
        this.sendPacketToAll(new ChangeHostNotify(this.host));
    }

    public void sendPacketToAll(Packet<?> packet) {
        this.sendPacketToAll(packet, null);
    }

    public void sendPacketToAll(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        synchronized (this.spiders) {
            this.spiders.forEach(serverSpider -> serverSpider.sendPacket(packet, callback));
        }
    }

    public void sendPacketToOthers(ServerSpider sender, Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        synchronized (this.spiders) {
            this.spiders.stream().filter(p -> !p.equals(sender)).forEach(serverSpider -> serverSpider.sendPacket(packet, callback));
        }
    }

    public void removeSpider(ServerSpider spider) {
        synchronized (this.spiders) {
            this.spiders.remove(spider);
            if (this.host == spider) {
                this.host = this.spiders.isEmpty() ? null : this.spiders.get(0);
            }
        }

        if (this.host != null) {
            this.sendPacketToAll(new ChangeHostNotify(this.host));
        }

        this.sendPacketToAll(new ChatNotify(String.format("%s[%s] がサーバーから退出しました", spider.getName(), spider.connection.getConnection().getAddress())));
    }

    public boolean isHost(String name) {
        return this.host != null && this.host.getName().equals(name);
    }

    public boolean changeHost(String name) {
        synchronized (this.spiders) {
            var target = this.spiders.stream().filter(serverSpider -> serverSpider.getName().equals(name)).findFirst();
            target.ifPresent(this::changeHost);
            return target.isPresent();
        }
    }

    public void changeHost(ServerSpider host) {
        this.host = host;
        this.sendPacketToAll(new ChangeHostNotify(this.host));
    }

    public ImmutableList<ServerSpider> getSpiders() {
        return ImmutableList.copyOf(this.spiders);
    }
}
