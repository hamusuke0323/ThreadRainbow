package com.hamusuke.threadr.client.network.listener.main.play;

import com.google.common.collect.Maps;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.game.card.LocalCard;
import com.hamusuke.threadr.client.game.card.RemoteCard;
import com.hamusuke.threadr.client.gui.component.panel.main.game.EndPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.game.PlayingPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.game.ResultPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.game.SelectingTopicPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.threadr.client.network.listener.main.ClientCommonPacketListenerImpl;
import com.hamusuke.threadr.client.network.listener.main.ClientRoomPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.mode.GameMode;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.GetTopicRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.GetTopicReq;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;
import java.util.Map;

public abstract class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TopicEntry EMPTY = new TopicEntry(-1, new Topic(List.of("お題のデータをサーバーに問い合わせています..."), "-", "-"));
    protected final Map<Integer, NumberCard> cardMap = Maps.newConcurrentMap();
    private TopicEntry topicEntry;
    private int unknownTopicId = -1;
    protected boolean selectingTopic;

    public ClientPlayPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, client.curRoom, connection);
        this.clientSpider = client.clientSpider;
    }

    public static ClientPlayPacketListenerImpl newListenerByGameMode(GameMode mode, ThreadRainbowClient client, Connection connection) {
        return switch (mode) {
            case SPIDERS_THREAD_V2 -> new ClientPlay4SpidersThreadV2GamePacketListener(client, connection);
            case THREAD_RAINBOW -> new ClientPlay4ThreadRainbowGamePacketListener(client, connection);
        };
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        super.handleChangeHost(packet);
        this.client.setPanel(this.client.getPanel());

        if (this.selectingTopic && this.client.amIHost()) {
            this.client.getMainWindow().topicListPanel.showChooseBtn();
        } else {
            this.client.getMainWindow().topicListPanel.hideChooseBtn();
        }
    }

    @Override
    public void handleGiveCard(LocalCardHandedNotify packet) {
        var card = new LocalCard(this.clientSpider, packet.num());
        this.clientSpider.setClientCard(card);
        this.cardMap.clear();
        this.cardMap.put(this.clientSpider.getId(), card);
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
                            spider.setClientCard(card);
                            this.cardMap.put(spider.getId(), card);
                        }
                    });
        }
    }

    @Override
    public void handleStartTopicSelection(StartTopicSelectionNotify packet) {
        this.selectingTopic = true;
        this.setTopic(packet.firstTopicId());

        if (this.client.amIHost()) {
            this.client.getMainWindow().topicListPanel.showChooseBtn();
        }
    }

    @Override
    public void handleSelectTopic(TopicChangeNotify packet) {
        this.setTopic(packet.topicId());
    }

    private void setTopic(int topicId) {
        var e = this.curRoom.getTopicList().getTopics().get(topicId);
        if (e == null) {
            LOGGER.warn("Probably the client could not synchronize topics with the server!");
            LOGGER.warn("Trying to send sync packet...");
            this.connection.sendPacket(new GetTopicReq(topicId));
            this.unknownTopicId = topicId;
            e = EMPTY;
        }

        this.setTopic(e);
    }

    private void setTopic(TopicEntry e) {
        if (e != EMPTY) {
            this.unknownTopicId = -1;
        }

        this.topicEntry = e;
        this.client.setPanel(new SelectingTopicPanel(this.topicEntry.topic()));
    }

    @Override
    public void handleGetTopic(GetTopicRsp packet) {
        super.handleGetTopic(packet);

        if (this.unknownTopicId >= 0) {
            this.unknownTopicId = -1;
            this.setTopic(packet.topicEntry());
        }
    }

    @Override
    public void handleStartMainGame(StartMainGameNotify packet) {
        this.selectingTopic = false;
        this.client.getMainWindow().topicListPanel.hideChooseBtn();

        this.client.model = new DefaultListModel<>();
        packet.cards().forEach(i -> {
            var card = this.getCardById(i);
            if (card == null) {
                LOGGER.warn("null card returned! should never happen.");
                return;
            }

            this.client.model.addElement(card);
        });

        this.client.setPanel(this.getPlayingPanel(this.topicEntry.topic()));
    }

    protected PlayingPanel getPlayingPanel(Topic topic) {
        return new PlayingPanel(topic);
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
                        if (s.getClientCard() != null) {
                            s.getClientCard().setNumber(packet.num());
                            s.getClientCard().setOut(packet.isOut());
                            s.getClientCard().uncover();
                        }
                    });
        }

        this.client.getPanel().repaint();
    }

    @Override
    public void handleGameEnd(GameEndNotify packet) {
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

    @Override
    public void handleStartMakingTeam(StartMakingTeamNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleTeamToggleSync(TeamToggleSyncNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleMakingTeamDone(MakingTeamDoneNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleTimerStart(TimerStartNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleTimerSync(TimerSyncNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleFinishButtonAck(FinishButtonAckNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleTeamFirstFinishGame(TeamFirstFinishGameNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleStartTeamResult(StartTeamResultNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleTeamCardData(TeamCardDataNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void handleFirstTeamResultDone(FirstTeamResultDoneNotify packet) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Nullable
    public NumberCard getCardById(int id) {
        return this.cardMap.get(id);
    }
}
