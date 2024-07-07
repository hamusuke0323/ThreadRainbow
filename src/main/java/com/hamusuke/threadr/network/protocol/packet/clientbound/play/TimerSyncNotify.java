package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TimerSyncNotify(int serverTimerTicks) implements Packet<ClientPlayPacketListener> {
    public TimerSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.serverTimerTicks);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTimerSync(this);
    }
}
