package com.hamusuke.threadr.server.network.listener.info;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.threadr.network.protocol.packet.c2s.info.ServerInfoRequestC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.info.ServerInfoResponseS2CPacket;

public class ServerInfoPacketListenerImpl implements ServerInfoPacketListener {
    private final Connection connection;

    public ServerInfoPacketListenerImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleInfoReq(ServerInfoRequestC2SPacket packet) {
        this.connection.sendPacket(new ServerInfoResponseS2CPacket(Constants.PROTOCOL_VERSION, packet.clientTime()));
    }

    @Override
    public void onDisconnected(String msg) {
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
