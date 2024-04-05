package com.hamusuke.threadr.room;

import com.hamusuke.threadr.server.network.ServerSpider;

public class RoomInvitation {
    private static final int EXPIRE_TICK = 200;
    private final ServerSpider owner;
    private final ServerSpider target;
    private int expireTicks;

    public RoomInvitation(ServerSpider owner, ServerSpider target) {
        this.owner = owner;
        this.target = target;
    }

    public void sendInvitation() {
        this.expireTicks = EXPIRE_TICK;

    }

    public void tick() {
        if (this.expireTicks > 0) {
            this.expireTicks--;
            if (this.expireTicks <= 0) {
                this.expire();
            }
        }
    }

    private void expire() {
    }
}
