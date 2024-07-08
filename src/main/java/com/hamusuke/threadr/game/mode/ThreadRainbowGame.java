package com.hamusuke.threadr.game.mode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.threadr.game.card.Cards;
import com.hamusuke.threadr.game.team.TeamEntry;
import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.*;
import com.hamusuke.threadr.server.game.team.ServerTeam;
import com.hamusuke.threadr.server.game.team.ServerTeamEntry;
import com.hamusuke.threadr.server.network.ServerSpider;
import com.hamusuke.threadr.server.network.listener.main.play.ServerPlay4ThreadRainbowGamePacketListener;
import com.hamusuke.threadr.server.room.ServerRoom;
import com.hamusuke.threadr.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class ThreadRainbowGame implements Game {
    public static final int ONE_MINUTE_TICKS = 20 * 60;
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerRoom room;
    private final Random random = new SecureRandom();
    private final List<ServerSpider> spiders;
    private final Cards blueTeamCards;
    private final Cards redTeamCards;
    private final ServerTeam team;
    private Status status = Status.NONE;
    private TopicEntry topic;
    private int timer;
    private boolean timerStarted;
    private TeamType firstFinishedTeam;
    private byte lastOkCardNum;
    private boolean firstTeamResultFinished;
    private int blueTeamOutNum;
    private int redTeamOutNum;

    public ThreadRainbowGame(ServerRoom room, List<ServerSpider> spidersToPlay) {
        this.room = room;
        this.spiders = Collections.synchronizedList(Lists.newArrayList(spidersToPlay));
        this.blueTeamCards = new Cards(room.getDeck());
        this.redTeamCards = new Cards(room.getDeck());
        this.team = new ServerTeam(spidersToPlay);
    }

    @Override
    public void tick() {
        if (this.timerStarted && this.timer > 0) {
            this.timer--;

            if (this.timer % 200 == 0 && this.firstFinishedTeam != null) {
                var notify = new TimerSyncNotify(this.timer);
                switch (this.firstFinishedTeam) {
                    case BLUE -> this.team.sendPacketToRedTeam(notify);
                    case RED -> this.team.sendPacketToBlueTeam(notify);
                }
            }

            if (this.timer <= 0) {
                this.finishGame();
            }
        }
    }

    @Override
    public void start() {
        if (this.status == Status.NONE) {
            this.sendPacketToAllInGame(new ChatNotify("ゲームを開始します"));
            this.nextStatus();
            this.makeTeam();
        }
    }

    private void makeTeam() {
        this.sendPacketToAllInGame(new ChatNotify("チーム分けをしてください"));
        this.sendPacketToAllInGame(new StartMakingTeamNotify(this.team.getTeamEntries().values().stream().map(TeamEntry::toSerializer).toList()));
    }

    public void finishMakingTeam() {
        if (this.status != Status.MAKING_TEAM || this.invalidateTeam()) {
            return;
        }

        this.nextStatus();
        this.sendPacketToAllInGame(new MakingTeamDoneNotify(this.team.getTeamEntries().values().stream().map(TeamEntry::toSerializer).toList()));
        this.sendPacketToAllInGame(new ChatNotify("チームが決まりました"));
        this.startTopicSelection();
    }

    private boolean invalidateTeam() {
        int blues = this.team.getBlueSpiders().size();
        int reds = this.team.getRedSpiders().size();

        if (blues <= 1 || reds <= 1) {
            this.sendPacketToAllInGame(new ChatNotify("各チーム2人以上必要です！"));
            return true;
        }

        return false;
    }

    public void toggleTeam(ServerTeamEntry teamEntry) {
        if (this.status != Status.MAKING_TEAM || !this.team.getTeamEntries().containsKey(teamEntry.getSpider())) {
            return;
        }

        this.team.getTeamEntries().get(teamEntry.getSpider()).changeTeam(teamEntry.getType());
        ((ServerPlay4ThreadRainbowGamePacketListener) teamEntry.getSpider().connection).myTeamType = teamEntry.getType();
        this.sendPacketToAllInGame(new TeamToggleSyncNotify(teamEntry.toSerializer()));
    }

    private void startTopicSelection() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        if (this.chooseRandomTopic()) {
            return;
        }

        this.sendPacketToAllInGame(new StartTopicSelectionNotify(this.topic.id()));
        this.sendPacketToAllInGame(new ChatNotify("ホストはお題を再度選ぶこともできます"));
    }

    public void changeTopic() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        if (this.chooseRandomTopic()) {
            return;
        }

        this.sendPacketToAllInGame(new TopicChangeNotify(this.topic.id()));
    }

    protected boolean chooseRandomTopic() {
        var topics = this.room.getTopicList().getTopicEntries();
        if (topics.isEmpty()) {
            this.sendPacketToAllInGame(new ChatNotify("この部屋で利用可能なお題がありません"));
            this.sendPacketToAllInGame(new ChatNotify("ホストはお題を作成できます"));
            return true;
        }

        this.topic = Util.chooseRandom(topics, this.random);
        return false;
    }

    public void decideTopic() {
        if (this.status != Status.SELECTING_TOPIC) {
            return;
        }

        this.spiders.forEach(spider -> spider.sendPacket(new ChatNotify("お題が決まりました")));
        this.nextStatus();
        this.giveOutCards();
    }

    private void giveOutCards() {
        var blues = this.team.getBlueSpiders();
        this.blueTeamCards.giveOutCards(blues);
        blues.forEach(spider -> spider.sendPacket(new LocalCardHandedNotify(spider.getHoldingCard().num())));
        blues.forEach(spider -> this.team.sendPacketToOthersInBlueTeam(spider, new RemoteCardGivenNotify(spider)));

        var reds = this.team.getRedSpiders();
        this.redTeamCards.giveOutCards(reds);
        reds.forEach(spider -> spider.sendPacket(new LocalCardHandedNotify(spider.getHoldingCard().num())));
        reds.forEach(spider -> this.team.sendPacketToOthersInRedTeam(spider, new RemoteCardGivenNotify(spider)));

        this.sendPacketToAllInGame(new ChatNotify("カードを配りました"));
    }

    public void startMainGame() {
        if (this.status != Status.GIVING_OUT_CARDS) {
            return;
        }

        this.nextStatus();
        this.team.sendPacketToBlueTeam(new StartMainGameNotify(this.blueTeamCards.getCards()));
        this.team.sendPacketToRedTeam(new StartMainGameNotify(this.redTeamCards.getCards()));
        this.sendPacketToAllInGame(new ChatNotify("ゲームが始まりました"));
        this.sendPacketToAllInGame(new ChatNotify("お題に沿って「たとえ」てチームで小さい順に並べましょう"));
        this.sendPacketToAllInGame(new ChatNotify("完成したら，チーム全員は「完成」ボタンをクリックしてください"));
    }

    public synchronized void moveCard(TeamType type, ServerSpider operator, int from, int to) {
        if (this.status != Status.PLAYING) {
            return;
        }

        if (!(
                switch (type) {
                    case BLUE -> this.team.getBlueSpiders();
                    case RED -> this.team.getRedSpiders();
                }
        ).contains(operator) || this.firstFinishedTeam == type) {
            operator.sendError("カードを動かせません");
            return;
        }

        try {
            switch (type) {
                case BLUE -> {
                    this.blueTeamCards.moveCard(from, to);
                    this.team.sendPacketToBlueTeam(new CardMoveNotify(from, to));
                }
                case RED -> {
                    this.redTeamCards.moveCard(from, to);
                    this.team.sendPacketToRedTeam(new CardMoveNotify(from, to));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn("The card moved wrongly!", e);
            operator.sendError("カードが変なところに移動したので操作をキャンセルしました");
        }
    }

    public void onFinishBtnPressed(TeamType presserTeam, ServerSpider presser) {
        if (this.status != Status.PLAYING) {
            return;
        }

        this.team.pressFinishBtn(presserTeam, presser);
        presser.sendPacket(new FinishButtonAckNotify());
        switch (presserTeam) {
            case BLUE ->
                    this.team.sendPacketToBlueTeam(new TeamFinishButtonPressNumSyncNotify(this.team.getBlueFinishBtnPressersNum(), this.team.getBlueSpiders().size()));
            case RED ->
                    this.team.sendPacketToRedTeam(new TeamFinishButtonPressNumSyncNotify(this.team.getRedFinishBtnPressersNum(), this.team.getRedSpiders().size()));
        }

        this.checkIfTeamFinished();
    }

    private void checkIfTeamFinished() {
        if (this.status != Status.PLAYING) {
            return;
        }

        boolean blueFinished = this.team.isBlueTeamFinished();
        boolean redFinished = this.team.isRedTeamFinished();
        if (!this.timerStarted && (blueFinished || redFinished)) {
            if (blueFinished) {
                this.firstFinishedTeam = TeamType.BLUE;
                this.team.sendPacketToBlueTeam(new TeamFirstFinishGameNotify());
            } else {
                this.firstFinishedTeam = TeamType.RED;
                this.team.sendPacketToRedTeam(new TeamFirstFinishGameNotify());
            }

            this.startTimer(packet -> {
                if (blueFinished) {
                    this.team.sendPacketToRedTeam(packet);
                } else {
                    this.team.sendPacketToBlueTeam(packet);
                }
            });
        } else if (this.timerStarted && blueFinished && redFinished) {
            this.finishGame();
        }
    }

    private void startTimer(Consumer<Packet<?>> opponentNotifier) {
        if (this.timerStarted) {
            return;
        }

        this.timerStarted = true;
        this.timer = ONE_MINUTE_TICKS;
        opponentNotifier.accept(new ChatNotify("相手チームが完成宣言をしました"));
        opponentNotifier.accept(new ChatNotify("1分以内に完成させなければなりません！"));
        opponentNotifier.accept(new TimerStartNotify());
    }

    private void finishGame() {
        if (this.status != Status.PLAYING) {
            return;
        }

        this.nextStatus();
        this.timerStarted = false;
        this.timer = 0;
        this.sendPacketToAllInGame(new FinishMainGameNotify());
        this.sendPacketToAllInGame(new ChatNotify("最初に完成させたチームからカードをめくります"));
        this.sendPacketToAllInGame(new ChatNotify("ホストはカードをめくってください"));
        this.sendPacketToAllInGame(new StartTeamResultNotify());

        this.sendPacketToAllInGame(new TeamCardDataNotify(this.firstFinishedTeam, this.firstFinishedTeam == TeamType.BLUE ? this.blueTeamCards.getCards() : this.redTeamCards.getCards()));
    }

    public void uncoverCard() {
        if (this.status != Status.RESULT) {
            return;
        }

        this.uncoverTeamCard(this.firstTeamResultFinished ? this.firstFinishedTeam.opposite() : this.firstFinishedTeam);
    }

    private void uncoverTeamCard(TeamType teamType) {
        var uncCard = (teamType == TeamType.BLUE ? this.blueTeamCards : this.redTeamCards).uncover();
        if (uncCard == null) {
            return;
        }

        ServerSpider owner = null;
        ServerSpider prev = null;
        for (var spider : this.spiders) {
            if (spider.getId() == uncCard.ownerId()) {
                owner = spider;
            } else if (spider.getId() == uncCard.prevId()) {
                prev = spider;
            }

            if (owner != null && prev != null) {
                break;
            }
        }

        boolean isOut = false;
        if (owner != null) {
            var card = owner.getHoldingCard();
            if (uncCard.isFirstCard()) {
                this.lastOkCardNum = card.getNumber();
            } else {
                if (card.getNumber() > this.lastOkCardNum) {
                    this.lastOkCardNum = card.getNumber();
                } else {
                    isOut = true;
                    if (teamType == TeamType.BLUE) {
                        this.blueTeamOutNum++;
                    } else {
                        this.redTeamOutNum++;
                    }
                }
            }

            this.sendPacketToAllInGame(new UncoverCardNotify(owner.getId(), card.getNumber(), isOut));
        }

        if (!uncCard.last()) {
            return;
        }

        if (this.firstTeamResultFinished) {
            this.onLastTeamResultFinished();
            return;
        }

        this.onFirstTeamResultFinished();
    }

    private void onFirstTeamResultFinished() {
        this.sendPacketToAllInGame(new TeamResultDoneNotify(this.firstFinishedTeam));
    }

    private void startResultingNextTeam() {
        this.sendPacketToAllInGame(new ChatNotify("最後に完成させたチームのカードをめくります"));
        this.sendPacketToAllInGame(new ChatNotify("ホストはカードをめくってください"));
        this.sendPacketToAllInGame(new StartTeamResultNotify());

        this.sendPacketToAllInGame(new TeamCardDataNotify(this.firstFinishedTeam.opposite(), this.firstFinishedTeam.opposite() == TeamType.BLUE ? this.blueTeamCards.getCards() : this.redTeamCards.getCards()));
    }

    private void onLastTeamResultFinished() {
        this.sendPacketToAllInGame(new TeamResultDoneNotify(this.firstFinishedTeam.opposite()));
    }

    public void onNextCommand() {
        if (!this.firstTeamResultFinished) {
            this.firstTeamResultFinished = true;
            this.startResultingNextTeam();
            return;
        }

        this.showFinalResult();
    }

    private void showFinalResult() {
        if (this.status != Status.RESULT) {
            return;
        }

        this.sendPacketToAllInGame(new ChatNotify("青チームのアウト数: " + this.blueTeamOutNum));
        this.sendPacketToAllInGame(new ChatNotify("赤チームのアウト数: " + this.redTeamOutNum));

        if (this.blueTeamOutNum == this.redTeamOutNum) {
            this.sendPacketToAllInGame(new ChatNotify("ドローです！"));
            this.endGame();
            return;
        }

        var wonTeam = this.blueTeamOutNum > this.redTeamOutNum ? TeamType.RED : TeamType.BLUE;
        var wonMsg = new ChatNotify("勝利です！");
        var lostMsg = new ChatNotify("残念、負けです...");
        switch (wonTeam) {
            case BLUE -> {
                this.team.sendPacketToBlueTeam(wonMsg);
                this.team.sendPacketToRedTeam(lostMsg);
            }
            case RED -> {
                this.team.sendPacketToRedTeam(wonMsg);
                this.team.sendPacketToBlueTeam(lostMsg);
            }
        }

        this.endGame();
    }

    private void endGame() {
        if (this.status != Status.RESULT) {
            return;
        }

        this.nextStatus();
        this.sendPacketToAllInGame(new GameEndNotify());
    }

    @Override
    public void restart() {
        if (this.status != Status.END) {
            return;
        }

        this.status = Status.NONE;
        this.resetAndSendRestart();

        this.start();
    }

    public void restartWithTheSameTeam() {
        if (this.status != Status.END) {
            return;
        }

        this.status = Status.SELECTING_TOPIC;
        this.resetAndSendRestart();

        this.sendPacketToAllInGame(new ChatNotify("ゲームを開始します"));
        this.sendPacketToAllInGame(new MakingTeamDoneNotify(this.team.getTeamEntries().values().stream().map(TeamEntry::toSerializer).toList()));
        this.startTopicSelection();
    }

    private void resetAndSendRestart() {
        this.firstFinishedTeam = null;
        this.redTeamOutNum = this.blueTeamOutNum = 0;
        this.firstTeamResultFinished = false;
        this.lastOkCardNum = 0;
        this.team.reset();
        this.room.getDeck().returnAllCards();
        this.sendPacketToAllInGame(new RestartGameNotify());
    }

    @Override
    public boolean setTopic(int topicId) {
        if (this.status != Status.SELECTING_TOPIC) {
            return true;
        }

        var topics = this.room.getTopics();
        if (!topics.containsKey(topicId)) {
            return false;
        }

        this.topic = topics.get(topicId);
        this.sendPacketToAllInGame(new TopicChangeNotify(this.topic.id()));
        return true;
    }

    @Override
    public List<ServerSpider> getPlayingSpiders() {
        return ImmutableList.copyOf(this.spiders);
    }

    @Override
    public synchronized void onSpiderLeft(ServerSpider spider) {
        if (!this.spiders.contains(spider)) {
            return;
        }

        this.spiders.remove(spider);
        if (this.spiders.isEmpty()) {
            this.room.endGame();
            return;
        }

        if (this.room.isHost(spider) && !this.spiders.isEmpty()) {
            this.room.changeHost(this.spiders.get(0));
        }

        this.sendPacketToAllInGame(new SpiderExitGameNotify(spider));
        this.sendPacketToAllInGame(new ChatNotify(spider.getName() + " がゲームをやめました"));

        this.removeSpiderFromTeam(spider);
        this.checkIfGameContinuable();
    }

    private void removeSpiderFromTeam(ServerSpider spider) {
        var e = this.team.removeSpider(spider);
        if (e == null) {
            return;
        }

        var cards = switch (e.getType()) {
            case BLUE -> this.blueTeamCards;
            case RED -> this.redTeamCards;
        };
        cards.remove(spider);
        this.checkIfTeamFinished();

        if (this.status != Status.RESULT) {
            return;
        }

        if (!this.firstTeamResultFinished && this.firstFinishedTeam == e.getType() && !cards.hasCoveredCards()) {
            this.onFirstTeamResultFinished();
        } else if (this.firstTeamResultFinished && this.firstFinishedTeam.opposite() == e.getType() && !cards.hasCoveredCards()) {
            this.onLastTeamResultFinished();
        }
    }

    private void checkIfGameContinuable() {
        if (this.status == Status.NONE || this.status == Status.MAKING_TEAM) {
            return;
        }

        int blues = this.team.getBlueSpiders().size();
        int reds = this.team.getRedSpiders().size();
        if (blues <= 1 || reds <= 1) {
            this.sendPacketToAllInGame(new ChatNotify("人数が少なすぎるためゲームを強制終了します"));
            this.room.endGame();
        }
    }

    @Override
    public void sendPacketToAllInGame(Packet<?> packet) {
        this.spiders.forEach(spider -> spider.sendPacket(packet));
    }

    private void nextStatus() {
        if (this.status == Status.END) {
            return;
        }

        this.status = this.status.next();
    }

    public Status getStatus() {
        return this.status;
    }

    public ServerTeam getTeam() {
        return this.team;
    }

    public enum Status {
        NONE,
        MAKING_TEAM,
        SELECTING_TOPIC,
        GIVING_OUT_CARDS,
        PLAYING,
        RESULT,
        END;

        private Status next() {
            var i = this.ordinal() + 1;
            return i < values().length ? values()[i] : NONE;
        }
    }
}
