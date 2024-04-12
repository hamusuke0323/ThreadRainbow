package com.hamusuke.threadr.server;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class SpiderManager {
    private final List<ServerSpider> spiders = Lists.newArrayList();
    private final List<ServerSpider> spiderList;

    public SpiderManager() {
        this.spiderList = Collections.unmodifiableList(this.spiders);
    }

    public boolean canJoin(ServerSpider serverSpider) {
        return serverSpider.isAuthorized();
    }

    public void addSpider(ServerSpider serverSpider) {
        synchronized (this.spiders) {
            this.spiders.add(serverSpider);
        }
    }

    public void sendPacketToAll(Packet<?> packet) {
        this.sendPacketToAll(packet, null);
    }

    public void sendPacketToAll(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        synchronized (this.spiders) {
            this.spiders.forEach(serverSpider -> serverSpider.sendPacket(packet, callback));
        }
    }

    public void removeSpider(ServerSpider spider) {
        synchronized (this.spiders) {
            this.spiders.remove(spider);
        }
    }

    public List<ServerSpider> getSpiders() {
        return this.spiderList;
    }
}
