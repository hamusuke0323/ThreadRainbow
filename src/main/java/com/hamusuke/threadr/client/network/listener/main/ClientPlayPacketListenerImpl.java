package com.hamusuke.threadr.client.network.listener.main;

import com.google.common.collect.Maps;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.game.card.LocalCard;
import com.hamusuke.threadr.client.game.card.RemoteCard;
import com.hamusuke.threadr.client.gui.component.panel.main.game.*;
import com.hamusuke.threadr.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Map;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Integer, NumberCard> cardMap = Maps.newConcurrentMap();
    private TopicEntry topicEntry;

    public ClientPlayPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, client.curRoom, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        super.handleChangeHost(packet);
        this.client.setPanel(this.client.getPanel());
    }

    @Override
    public void handleGiveCard(LocalCardHandedNotify packet) {
        var card = new LocalCard(this.clientSpider, packet.num());
        this.clientSpider.takeCard(card);
        this.cardMap.clear();
        this.cardMap.put(this.clientSpider.getId(), card);
        this.client.setPanel(new HandedCardPanel());
    }

    @Override
    public void handleRemoteCard(RemoteCardGivenNotify packet) {
        synchronized (this.curRoom.getSpiders()) {
            this.curRoom.getSpiders().stream()
                    .filter(s -> s.getId() == packet.id())
                    .findFirst()
                    .ifPresent(s -> {
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
        this.setTopic(packet.firstTopicId());
    }

    @Override
    public void handleSelectTopic(TopicChangeNotify packet) {
        this.setTopic(packet.topicId());
    }

    private void setTopic(int topicId) {
        this.topicEntry = this.curRoom.getTopicList().getTopics().get(topicId);
        if (this.topicEntry == null) {
            LOGGER.warn("Probably the client could not synchronize topics with the server!");
            LOGGER.warn("Trying to send sync packet...");
            // TODO
            return;
        }

        this.client.setPanel(new SelectingTopicPanel(this.topicEntry.topic()));
    }

    @Override
    public void handleStartMainGame(StartMainGameNotify packet) {
        this.client.model = new DefaultListModel<>();
        packet.cards().forEach(i -> {
            var card = this.getCardById(i);
            if (card == null) {
                LOGGER.warn("null card returned! should never happen.");
                return;
            }

            this.client.model.addElement(card);
        });

        this.client.setPanel(new PlayingPanel(this.topicEntry.topic()));
    }

    @Override
    public void handleCardMoved(CardMoveNotify packet) {
        if (this.client.model == null) {
            return;
        }

        var from = packet.from();
        var to = packet.to();

        var str = this.client.model.get(from);
        if (to < from) {
            this.client.model.remove(from);
            this.client.model.add(to, str);
        } else {
            this.client.model.add(to + 1, str);
            this.client.model.remove(from);
        }

        this.client.getPanel().repaint();
    }

    @Override
    public void handleMainGameFinish(FinishMainGameNotify packet) {
        this.client.setPanel(new ResultPanel());
    }

    @Override
    public void handleUncoverCard(UncoverCardNotify packet) {
        synchronized (this.curRoom.getSpiders()) {
            this.curRoom.getSpiders().stream()
                    .filter(s -> s.getId() == packet.id())
                    .findFirst()
                    .ifPresent(s -> {
                        if (s instanceof RemoteSpider r) {
                            r.getRemoteCard().setNumber(packet.num());
                            r.getRemoteCard().uncover();
                        } else if (s instanceof LocalSpider l) {
                            l.getLocalCard().uncover();
                        }
                    });
        }

        this.client.getPanel().repaint();

        if (!packet.last()) {
            return;
        }

        this.client.setPanel(new EndPanel());
    }

    @Override
    public void handleRestart(RestartGameNotify packet) {
        this.client.setPanel(new RoomPanel());
    }

    @Override
    public void handleExit(ExitGameNotify packet) {
        var listener = new ClientRoomPacketListenerImpl(this.client, this.connection);
        this.client.setPanel(new RoomPanel());
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }

    @Override
    public void handleSpiderExit(SpiderExitGameNotify packet) {
        var card = this.cardMap.get(packet.id());
        if (card != null) {
            if (this.client.model != null) {
                this.client.model.removeElement(card);
                this.client.getPanel().repaint();
            }

            this.cardMap.remove(packet.id());
        }
    }

    @Nullable
    public NumberCard getCardById(int id) {
        return this.cardMap.get(id);
    }
}
