package com.hamusuke.threadr.client.network.listener.info;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.ServerInfo;
import com.hamusuke.threadr.network.ServerInfo.Status;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.info.ServerInfoResponseS2CPacket;
import com.hamusuke.threadr.util.Util;

public class ClientInfoPacketListenerImpl implements ClientInfoPacketListener {
    private static final int TIMEOUT_TICKS = 100;
    private final ThreadRainbowClient client;
    private final Connection connection;
    private final ServerInfo target;
    private int timeoutTicks = TIMEOUT_TICKS;

    public ClientInfoPacketListenerImpl(ThreadRainbowClient client, Connection connection, ServerInfo target) {
        this.client = client;
        this.connection = connection;
        this.target = target;
    }

    @Override
    public void tick() {
        if (this.timeoutTicks > 0 && this.target.status == Status.CONNECTING) {
            this.timeoutTicks--;
            if (this.timeoutTicks <= 0) {
                this.target.status = Status.FAILED;
                this.connection.disconnect("Failure");
            }
        }
    }

    @Override
    public void onDisconnected(String msg) {
        if (!msg.equals("Success")) {
            this.target.status = Status.FAILED;
        }

        this.client.onServerInfoChanged();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void handleInfoRsp(ServerInfoResponseS2CPacket packet) {
        this.target.protocolVersion = packet.protocolVersion();
        this.target.ping = (int) (Util.getMeasuringTimeMs() - packet.clientTimeEcho());
        this.target.status = this.target.protocolVersion == Constants.PROTOCOL_VERSION ? Status.OK : Status.MISMATCH_PROTOCOL_VERSION;
        this.connection.disconnect("Success");
    }
}
