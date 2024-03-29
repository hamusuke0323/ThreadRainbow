package com.hamusuke.threadr.network;

import com.hamusuke.threadr.network.protocol.packet.Packet;

public interface PacketLogger {
    PacketLogger EMPTY = new PacketLogger() {
        @Override
        public void send(Packet<?> packet) {
        }

        @Override
        public void receive(Packet<?> packet) {
        }
    };

    void send(Packet<?> packet);

    void receive(Packet<?> packet);
}
