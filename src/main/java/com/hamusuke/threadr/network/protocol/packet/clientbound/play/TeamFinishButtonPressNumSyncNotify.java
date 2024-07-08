package com.hamusuke.threadr.network.protocol.packet.clientbound.play;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;

public record TeamFinishButtonPressNumSyncNotify(int pressNum,
                                                 int maxPressNum) implements Packet<ClientPlayPacketListener> {
    public TeamFinishButtonPressNumSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.pressNum);
        buf.writeVariableInt(this.maxPressNum);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTeamFinishButtonPressNumSync(this);
    }
}
