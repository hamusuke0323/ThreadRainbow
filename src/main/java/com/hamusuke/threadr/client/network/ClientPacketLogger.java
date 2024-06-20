package com.hamusuke.threadr.client.network;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.PacketLogger;

public record ClientPacketLogger(ThreadRainbowClient client) implements PacketLogger {
    public ClientPacketLogger(ThreadRainbowClient client) {
        this.client = client;
        this.client.packetLogTable.clear();
    }

    @Override
    public void send(PacketDetails details) {
        if (this.client.isPacketTrash(details.packet())) {
            return;
        }

        this.client.packetLogTable.addSent(details);
    }

    @Override
    public void receive(PacketDetails details) {
        if (this.client.isPacketTrash(details.packet())) {
            return;
        }

        this.client.packetLogTable.addReceived(details);
    }
}
