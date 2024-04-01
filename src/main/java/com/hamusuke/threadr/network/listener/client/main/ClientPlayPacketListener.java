package com.hamusuke.threadr.network.listener.client.main;

import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    void handleGiveCard(LocalCardHandedNotify packet);

    void handleRemoteCard(RemoteCardGivenNotify packet);

    void handleStartTopicSelection(StartTopicSelectionNotify packet);

    void handleSelectTopic(TopicChangeNotify packet);

    void handleStartMainGame(StartMainGameNotify packet);

    void handleCardMoved(CardMoveNotify packet);

    void handleMainGameFinish(FinishMainGameNotify packet);

    void handleUncoverCard(UncoverCardNotify packet);

    void handleRestart(RestartGameNotify packet);

    void handleExit(ExitGameNotify packet);

    void handleSpiderExit(SpiderExitGameNotify packet);
}
