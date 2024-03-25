package com.hamusuke.threadr.network.protocol;

public enum PacketDirection {
    CLIENTBOUND,
    SERVERBOUND;

    public PacketDirection getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }
}
