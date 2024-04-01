package com.hamusuke.threadr.client.network.listener.main;

import com.google.common.collect.Maps;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.LocalCard;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.card.RemoteCard;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public MainWindow mainWindow;
    private final Map<Integer, NumberCard> cardMap = Maps.newConcurrentMap();

    public ClientPlayPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        super.handleChangeHost(packet);

        this.mainWindow.onChangeHost();
    }

    @Override
    public void handleGiveCard(LocalCardHandedNotify packet) {
        var card = new LocalCard(this.clientSpider, packet.num());
        this.clientSpider.takeCard(card);
        this.cardMap.clear();
        this.cardMap.put(this.clientSpider.getId(), card);
        this.mainWindow.card();
    }

    @Override
    public void handleRemoteCard(RemoteCardGivenNotify packet) {
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.stream().filter(s -> s.getId() == packet.id()).findFirst().ifPresent(s -> {
                if (s instanceof LocalSpider) {
                    LOGGER.warn("Remote card came on me, should never happen!");
                } else if (s instanceof RemoteSpider spider) {
                    var card = new RemoteCard(spider);
                    spider.haveRemoteCard(card);
                    this.cardMap.put(spider.getId(), card);
                }
            });
        }
    }

    @Override
    public void handleStartTopicSelection(StartTopicSelectionNotify packet) {
        this.mainWindow.topic(packet.firstTopic());
    }

    @Override
    public void handleSelectTopic(TopicChangeNotify packet) {
        this.mainWindow.topic(packet.topic());
    }

    @Override
    public void handleStartMainGame(StartMainGameNotify packet) {
        this.mainWindow.lineupCard(packet.cards());
    }

    @Override
    public void handleCardMoved(CardMoveNotify packet) {
        this.mainWindow.onCardMoved(packet.from(), packet.to());
    }

    @Override
    public void handleMainGameFinish(FinishMainGameNotify packet) {
        this.mainWindow.onMainGameFinished();
    }

    @Override
    public void handleUncoverCard(UncoverCardNotify packet) {
        this.mainWindow.onUncovered(packet.id(), packet.num(), packet.last());
    }

    @Override
    public void handleRestart(RestartGameNotify packet) {
        this.mainWindow.reset();
        this.client.spiderTable.removeCardNumCol();
    }

    @Override
    public void handleExit(ExitGameNotify packet) {
        int id = this.hostId;
        var listener = new ClientLobbyPacketListenerImpl(this.client, this.connection);
        listener.hostId = id;
        listener.mainWindow = this.mainWindow;
        this.mainWindow.reset();
        this.mainWindow.lobby();
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }

    @Override
    public void handleSpiderExit(SpiderExitGameNotify packet) {
        var card = this.cardMap.get(packet.id());
        if (card != null) {
            this.mainWindow.onSpiderLeft(card);
            this.cardMap.remove(packet.id());
        }
    }

    @Nullable
    public NumberCard getCardById(int id) {
        return this.cardMap.get(id);
    }
}
