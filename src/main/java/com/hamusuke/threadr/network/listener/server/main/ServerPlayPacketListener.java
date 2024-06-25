package com.hamusuke.threadr.network.listener.server.main;

import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ChooseTopicReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.MoveCardReq;

public interface ServerPlayPacketListener extends ServerCommonPacketListener {
    void handleClientCommand(ClientCommandReq packet);

    void handleMoveCard(MoveCardReq packet);

    void handleChooseTopic(ChooseTopicReq packet);
}
