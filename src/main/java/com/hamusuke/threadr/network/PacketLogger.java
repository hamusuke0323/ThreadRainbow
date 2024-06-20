package com.hamusuke.threadr.network;

import com.hamusuke.threadr.network.protocol.packet.Packet;

public interface PacketLogger {
    PacketLogger EMPTY = new PacketLogger() {
        @Override
        public void send(PacketDetails details) {
        }

        @Override
        public void receive(PacketDetails details) {
        }
    };

    void send(PacketDetails details);

    void receive(PacketDetails details);

    record PacketDetails(Packet<?> packet, int size) {
    }
}
