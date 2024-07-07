package com.hamusuke.threadr.client.network.listener.main.play;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.game.card.AbstractClientCard;
import com.hamusuke.threadr.client.game.card.RemoteCard;
import com.hamusuke.threadr.client.gui.component.list.TeamList;
import com.hamusuke.threadr.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.threadr.client.gui.component.panel.main.game.*;
import com.hamusuke.threadr.client.network.spider.AbstractClientSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.team.TeamEntry;
import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class ClientPlay4ThreadRainbowGamePacketListener extends ClientPlayPacketListenerImpl {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TeamList teamList;
    private TeamType myTeam;
    private DefaultListModel<NumberCard> teamCards;

    public ClientPlay4ThreadRainbowGamePacketListener(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.teamList = new TeamList(client);
    }

    @Override
    public void handleGiveCard(LocalCardHandedNotify packet) {
        super.handleGiveCard(packet);
        this.selectingTopic = false;
        this.client.getMainWindow().topicListPanel.hideChooseBtn();
        this.client.setPanel(new TeamHandedCardPanel());
    }

    @Override
    public void handleStartMakingTeam(StartMakingTeamNotify packet) {
        this.teamList.clear();
        this.teamList.addTeamEntries(packet.serializers().stream()
                .map(serializer -> TeamEntry.deserialize(serializer, this.curRoom::getSpider))
                .filter(teamEntry -> Objects.nonNull(teamEntry.getSpider()))
                .toList());
        var panel = new MakingTeamPanel(this.teamList);
        this.client.setPanel(panel);
    }

    @Override
    public void handleTeamToggleSync(TeamToggleSyncNotify packet) {
        var e = TeamEntry.deserialize(packet.teamSerializer(), this.curRoom::getSpider);
        this.teamList.getTeamEntries().stream()
                .filter(teamEntry -> teamEntry.getSpider() == e.getSpider())
                .findFirst()
                .ifPresent(teamEntry -> teamEntry.changeTeam(e.getType()));
    }

    @Override
    public void handleMakingTeamDone(MakingTeamDoneNotify packet) {
        var entries = packet.teamSerializers().stream()
                .map(teamSerializer -> TeamEntry.deserialize(teamSerializer, this.curRoom::getSpider))
                .filter(teamEntry -> Objects.nonNull(teamEntry.getSpider()))
                .toList();

        this.teamList.onMakingTeamDone(entries);

        entries.forEach(teamEntry -> {
            if (teamEntry.getSpider() == this.clientSpider) {
                this.clientSpider.myTeam = this.myTeam = teamEntry.getType();
            } else if (teamEntry.getSpider() instanceof AbstractClientSpider spider) {
                spider.myTeam = teamEntry.getType();
            }
        });

        this.client.spiderTable.setOverrideRenderer(model -> {
            synchronized (this.curRoom.getSpiders()) {
                List<AbstractClientSpider> opponentMember = Lists.newArrayList();
                List<AbstractClientSpider> allSpiders = Lists.newArrayList(this.curRoom.getSpiders());
                this.curRoom.getSpiders().stream()
                        .filter(spider -> spider.myTeam != this.myTeam)
                        .forEach(opponentTeamSpider -> {
                            allSpiders.remove(opponentTeamSpider);
                            opponentMember.add(opponentTeamSpider);
                        });
                allSpiders.addAll(opponentMember);
                allSpiders.forEach(spider -> {
                    if (model.getColumnCount() == 2) {
                        var num = "";

                        if (spider.getClientCard() != null) {
                            num = spider.getClientCard().toString();
                        }

                        model.addRow(new Object[]{spider, num});
                    } else {
                        model.addRow(new Object[]{spider});
                    }
                });
            }
        });
    }

    @Override
    public void handleStartMainGame(StartMainGameNotify packet) {
        super.handleStartMainGame(packet);
        SwingUtilities.invokeLater(this.client.spiderTable::addCardNumCol);
    }

    @Override
    protected PlayingPanel getPlayingPanel(Topic topic) {
        return new TeamPlayingPanel(topic);
    }

    @Override
    public void handleTimerStart(TimerStartNotify packet) {
        if (this.client.getPanel() instanceof TeamPlayingPanel panel) {
            panel.startTimer();
            panel.placeTimer();
        }
    }

    @Override
    public void handleTimerSync(TimerSyncNotify packet) {
        if (this.client.getPanel() instanceof TeamPlayingPanel panel) {
            panel.syncTimerWithServer(packet.serverTimerTicks());
        }
    }

    @Override
    public void handleFinishButtonAck(FinishButtonAckNotify packet) {
        if (this.client.getPanel() instanceof TeamPlayingPanel panel) {
            panel.removeFinishBtn();
        }
    }

    @Override
    public void handleTeamFirstFinishGame(TeamFirstFinishGameNotify packet) {
        this.client.setPanel(new WaitOpponentTeamPanel());
    }

    @Override
    public void handleStartTeamResult(StartTeamResultNotify packet) {
        this.client.setPanel(new CenteredMessagePanel("サーバーからカードの情報が送られてくるのを待っています..."));
    }

    @Override
    public void handleTeamCardData(TeamCardDataNotify packet) {
        boolean isMyTeam = packet.teamType() == this.myTeam;
        this.teamCards = new DefaultListModel<>();
        packet.cards().forEach(i -> {
            var card = this.getCardById(i);
            if (card == null) {
                if (isMyTeam) {
                    LOGGER.warn("null card returned! should never happen.");
                    return;
                }

                if (this.curRoom.getSpider(i) instanceof RemoteSpider remote) {
                    card = new RemoteCard(remote);
                    remote.setClientCard((AbstractClientCard) card);
                    this.cardMap.put(i, card);
                }
            }

            this.teamCards.addElement(card);
        });

        this.client.setPanel(new TeamResultPanel(this.teamCards, isMyTeam));
    }

    @Override
    public void handleFirstTeamResultDone(FirstTeamResultDoneNotify packet) {
        this.client.setPanel(new TeamNextResultPanel(this.teamCards, packet.teamType() == this.myTeam));
    }

    @Override
    public void handleSpiderExit(SpiderExitGameNotify packet) {
        var card = this.getCardById(packet.id());
        if (card != null && this.teamCards != null) {
            this.teamCards.removeElement(card);
        }

        synchronized (this.curRoom.getSpiders()) {
            this.curRoom.getSpiders().stream()
                    .filter(spider -> spider.getId() == packet.id())
                    .findFirst()
                    .ifPresent(spider -> spider.myTeam = null);
        }

        super.handleSpiderExit(packet);
    }

    @Override
    public void handleGameEnd(GameEndNotify packet) {
        this.client.setPanel(new TeamEndPanel());
    }
}
