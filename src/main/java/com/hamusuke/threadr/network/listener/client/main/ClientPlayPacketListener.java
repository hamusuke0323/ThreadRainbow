package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.s2c.play.*;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    void handleGiveCard(GiveLocalCardS2CPacket packet);

    void handleRemoteCard(RemoteCardGivenS2CPacket packet);

    void handleStartTopicSelection(StartTopicSelectionS2CPacket packet);

    void handleSelectTopic(SelectTopicS2CPacket packet);

    void handleStartMainGame(StartMainGameS2CPacket packet);

    void handleCardMoved(CardMovedS2CPacket packet);

    void handleMainGameFinish(MainGameFinishedS2CPacket packet);
}
