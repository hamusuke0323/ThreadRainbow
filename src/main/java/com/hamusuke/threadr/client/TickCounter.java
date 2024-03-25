package com.hamusuke.threadr.client;

public class TickCounter {
    private final float tickTime;
    private float tickDelta;
    private float lastDuration;
    private long prevTimeMillis;

    public TickCounter(float tps, long timeMillis) {
        this.tickTime = 1000.0F / tps;
        this.prevTimeMillis = timeMillis;
    }

    public int beginLoopTick(long timeMillis) {
        this.lastDuration = (float) (timeMillis - this.prevTimeMillis) / this.tickTime;
        this.prevTimeMillis = timeMillis;
        this.tickDelta += this.lastDuration;
        int i = (int) this.tickDelta;
        this.tickDelta -= (float) i;
        return i;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    public float getLastDuration() {
        return this.lastDuration;
    }
}
