package com.hamusuke.threadr.client.network;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.PacketLogger;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record ClientPacketLogger(ThreadRainbowClient client) implements PacketLogger {
    public ClientPacketLogger(ThreadRainbowClient client) {
        this.client = client;
        this.client.packetLogTable.clear();
    }

    @Override
    public void send(Packet<?> packet) {
        if (this.client.isPacketTrash(packet)) {
            return;
        }

        this.client.packetLogTable.addSent(packet);
    }

    @Override
    public void receive(Packet<?> packet) {
        if (this.client.isPacketTrash(packet)) {
            return;
        }

        this.client.packetLogTable.addReceived(packet);
    }
}
