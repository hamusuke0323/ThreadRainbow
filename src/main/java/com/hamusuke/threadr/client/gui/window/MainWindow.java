package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.ImageLabel;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.network.listener.main.ClientPlayPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.c2s.lobby.StartGameC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.List;

public class MainWindow extends Window {
    private static final Logger LOGGER = LogManager.getLogger();
    private JPanel south;
    private WindowState state = WindowState.LOBBY;
    private JButton startGame;
    private JPanel lobbyPanel;
    private JPanel cardPanel;
    private ImageLabel card;
    private JLabel cardNum;
    private JButton show;
    private JButton ack;
    private JLabel waitHost;
    private JButton selectTopic;
    private Topic topic;
    private JPanel topicPanel;
    private JButton decideTopic;
    private NumberCardList list;
    private JButton finish;
    private JPanel gamePanel;
    private JButton uncover;
    private JButton restart;
    private JPanel east;
    private JMenuItem packetLog;
    private JScrollPane logScroll;

    public MainWindow() {
        super("ロビー");
    }

    @Override
    public void init() {
        super.init();

        this.setTitle("ロビー - " + this.client.getAddresses());
        this.east = new JPanel(new FlowLayout());
        this.logScroll = new JScrollPane(this.client.packetLogTable);
        this.logScroll.setAutoscrolls(true);
        this.east.add(this.logScroll);
        this.createSouth();
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);
    }

    private void createSouth() {
        if (this.south != null) {
            this.south.setEnabled(false);
            this.south.setVisible(false);
            this.remove(this.south);
        }

        var layout = new GridBagLayout();
        this.south = new JPanel(layout);

        var chatBag = new GridBagLayout();
        var chatPanel = new JPanel(chatBag);
        addButton(chatPanel, this.client.chat.getTextArea(), chatBag, 0, 0, 1, 1, 1.0D);
        addButton(chatPanel, this.client.chat.getField(), chatBag, 0, 1, 1, 1, 0.125D);
        var table = new JScrollPane(this.client.spiderTable);

        if (this.topic != null) {
            addButton(this.south, this.topic.toPIPPanel(), layout, 0, 0, 1, 1, 1.0D);
            addButton(this.south, chatPanel, layout, 1, 0, 1, 1, 1.0D);
            addButton(this.south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        } else {
            addButton(this.south, chatPanel, layout, 0, 0, 2, 1, 1.0D);
            addButton(this.south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        }

        this.south.setPreferredSize(new Dimension(100, this.getHeight() / 4));
        this.add(this.south, BorderLayout.SOUTH);
        this.revalidate();
    }

    public void lobby() {
        this.setTitle("ロビー - " + this.client.getAddresses());

        if (this.lobbyPanel != null) {
            this.lobbyPanel.setVisible(false);
            this.remove(this.lobbyPanel);
        }
        if (this.startGame != null) {
            this.startGame.setVisible(false);
        }

        this.state = WindowState.LOBBY;
        if (this.menu != null) {
            this.menu.setVisible(false);
            this.remove(this.menu);
        }
        this.menu = this.createMenuBar();
        this.add(this.menu, BorderLayout.NORTH);
        this.client.spiderTable.removeCardNumCol();
        this.lobbyPanel = new JPanel(new FlowLayout());

        if (this.amIHost()) {
            this.startGame = new JButton("始める");
            this.startGame.setActionCommand("start");
            this.startGame.addActionListener(this);
            this.lobbyPanel.add(this.startGame);
        }

        this.add(this.lobbyPanel, BorderLayout.CENTER);
        this.revalidate();
    }

    public void rmLobby() {
        this.lobbyPanel.setVisible(false);
        this.lobbyPanel.setEnabled(false);
        this.remove(this.lobbyPanel);
        if (this.startGame != null) {
            this.startGame.setVisible(false);
        }
        this.startGame = null;
        this.lobbyPanel = null;
        this.state = WindowState.STARTED;

        if (this.menu != null) {
            this.menu.setVisible(false);
            this.remove(this.menu);
        }
        this.menu = this.createMenuBar();
        this.add(this.menu, BorderLayout.NORTH);

        this.revalidate();
    }

    public void card() {
        if (this.cardPanel != null) {
            return;
        }

        this.setTitle("ゲーム - 配られたカードの数字を確認 " + this.client.getAddresses());
        this.card = new ImageLabel("/card.jpg");
        this.card.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        this.cardNum = new JLabel(this.client.clientSpider.getLocalCard().getNumber() + "", SwingConstants.CENTER);
        this.cardNum.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        this.cardNum.setVisible(false);
        this.show = new JButton("数字を見る");
        this.show.setActionCommand("show");
        this.show.addActionListener(this);
        this.ack = new JButton("OK!");
        this.ack.setEnabled(false);
        this.ack.setVisible(false);
        this.ack.setActionCommand("ack");
        this.ack.addActionListener(this);
        var layout = new GridBagLayout();
        this.cardPanel = new JPanel(layout);
        addButton(this.cardPanel, this.card, layout, 0, 0, 1, 1, 1.0D);
        addButton(this.cardPanel, this.cardNum, layout, 0, 0, 1, 1, 1.0D);
        addButton(this.cardPanel, this.show, layout, 0, 1, 1, 1, 0.125D);
        addButton(this.cardPanel, this.ack, layout, 0, 1, 1, 1, 0.125D);
        this.add(this.cardPanel, BorderLayout.CENTER);
        this.revalidate();
    }

    private void rmCard() {
        this.cardNum.setVisible(false);
        this.ack.setVisible(false);
        this.remove(this.cardPanel);
        this.card = null;
        this.cardNum = null;
        this.show = null;
        this.ack = null;
        this.cardPanel = null;
        this.revalidate();
    }

    private void showCard() {
        this.show.setEnabled(false);
        this.show.setVisible(false);
        this.card.setVisible(false);
        this.cardNum.setVisible(true);
        this.ack.setEnabled(true);
        this.ack.setVisible(true);
        this.client.spiderTable.addCardNumCol();
    }

    private void ackCard() {
        this.state = WindowState.WAITING_HOST;
        this.rmCard();
        this.ackCardPost();
    }

    private void ackCardPost() {
        if (this.waitHost != null) {
            this.remove(this.waitHost);
        }
        if (this.selectTopic != null) {
            this.remove(this.selectTopic);
        }

        if (this.amIHost()) {
            this.selectTopic = new JButton("お題を選ぶ");
            this.selectTopic.setActionCommand("select");
            this.selectTopic.addActionListener(this);
            this.add(this.selectTopic, BorderLayout.CENTER);
        } else {
            this.waitHost = new JLabel("ホストが次に進むのを待っています...");
            this.add(this.waitHost, BorderLayout.CENTER);
        }

        this.revalidate();
    }

    private void startSelectingTopic() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.START_SELECTING_TOPIC));
    }

    public void topic(Topic topic) {
        if (this.state != WindowState.SELECTING_TOPIC) {
            this.setTitle("ゲーム - お題決定 " + this.client.getAddresses());

            if (this.state != WindowState.WAITING_HOST) {
                this.showCard();
                this.ackCard();
            }

            if (this.waitHost != null) {
                this.waitHost.setVisible(false);
                this.remove(this.waitHost);
            }

            this.state = WindowState.SELECTING_TOPIC;
        }

        this.topic = topic;
        this.addCompForTopic();
    }

    private void addCompForTopic() {
        this.rmTopic();

        if (this.amIHost()) {
            this.selectTopic = new JButton("もう一度選ぶ");
            this.selectTopic.setActionCommand("reselect");
            this.selectTopic.addActionListener(this);
            this.decideTopic = new JButton("決定");
            this.decideTopic.setActionCommand("decide");
            this.decideTopic.addActionListener(this);
            this.topicPanel = this.topic.toPanel(List.of(this.selectTopic, this.decideTopic));
            this.add(this.topicPanel, BorderLayout.CENTER);
        } else {
            this.topicPanel = this.topic.toPanel();
            this.add(this.topicPanel, BorderLayout.CENTER);
        }

        this.revalidate();
    }

    private void reselect() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.RESELECT_TOPIC));
    }

    private void decideTopic() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.DECIDE_TOPIC));
    }

    public void rmTopic() {
        if (this.selectTopic != null) {
            this.selectTopic.setVisible(false);
            this.remove(this.selectTopic);
        }
        if (this.decideTopic != null) {
            this.decideTopic.setVisible(false);
            this.remove(this.decideTopic);
        }
        if (this.topicPanel != null) {
            this.topicPanel.setVisible(false);
            this.remove(this.topicPanel);
        }

        this.revalidate();
    }

    public void lineupCard(List<Integer> cards) {
        this.rmTopic();
        this.state = WindowState.PLAYING;
        this.setTitle("ゲーム - 「たとえ」て小さい順に並べる " + this.client.getAddresses());
        this.createSouth();

        this.list = new NumberCardList(this.client);
        var model = new DefaultListModel<NumberCard>();
        cards.forEach(i -> {
            var card = ((ClientPlayPacketListenerImpl) this.client.listener).getCardById(i);
            if (card == null) {
                LOGGER.warn("null card returned! should never happen.");
                return;
            }

            model.addElement(card);
        });
        this.list.setModel(model);

        this.addCompForPlay();
        this.revalidate();
    }

    private void addCompForPlay() {
        if (this.finish != null) {
            this.finish.setVisible(false);
        }
        if (this.gamePanel != null) {
            this.gamePanel.setVisible(false);
            this.remove(this.gamePanel);
        }

        var image = new ImageLabel("/zero.jpg");
        image.setMaximumSize(new Dimension(Constants.CARD_WIDTH, Integer.MAX_VALUE));
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(image);
        p.add(new JScrollPane(this.list));
        var l = new GridBagLayout();
        this.gamePanel = new JPanel(l);
        if (this.amIHost()) {
            this.finish = new JButton("完成！");
            this.finish.setActionCommand("finish");
            this.finish.addActionListener(this);
            addButton(this.gamePanel, p, l, 0, 0, 1, 1, 1.0D);
            addButton(this.gamePanel, this.finish, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(this.gamePanel, p, l, 0, 0, 1, 1, 1.0D);
        }

        this.add(this.gamePanel, BorderLayout.CENTER);
        this.revalidate();
    }

    public void onCardMoved(int from, int to) {
        this.list.moveCard(from, to);
    }

    private void finish() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.FINISH));
    }

    public void onMainGameFinished() {
        if (this.state != WindowState.PLAYING) {
            return;
        }

        this.state = WindowState.RESULT;
        this.setTitle("ゲーム - 結果発表 " + this.client.getAddresses());
        this.list.lock();
        this.addCompForResult();
    }

    private void addCompForResult() {
        if (this.finish != null) {
            this.finish.setVisible(false);
        }
        if (this.gamePanel != null) {
            this.gamePanel.setVisible(false);
            this.remove(this.gamePanel);
        }
        if (this.uncover != null) {
            this.uncover.setVisible(false);
        }

        var image = new ImageLabel("/zero.jpg");
        image.setMaximumSize(new Dimension(Constants.CARD_WIDTH, Integer.MAX_VALUE));
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(image);
        p.add(new JScrollPane(this.list));
        var l = new GridBagLayout();
        this.gamePanel = new JPanel(l);
        if (this.amIHost()) {
            this.uncover = new JButton("カードをめくる");
            this.uncover.setActionCommand("uncover");
            this.uncover.addActionListener(this);
            addButton(this.gamePanel, p, l, 0, 0, 1, 1, 1.0D);
            addButton(this.gamePanel, this.uncover, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(this.gamePanel, p, l, 0, 0, 1, 1, 1.0D);
        }

        this.add(this.gamePanel, BorderLayout.CENTER);
        this.revalidate();
    }

    private void uncover() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.UNCOVER));
    }

    public void onUncovered(int id, byte number, boolean last) {
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.stream().filter(s -> s.getId() == id).findFirst().ifPresent(s -> {
                if (s instanceof RemoteSpider r) {
                    r.getRemoteCard().setNumber(number);
                    r.getRemoteCard().uncover();
                } else if (s instanceof LocalSpider l) {
                    l.getLocalCard().uncover();
                }
            });
        }

        this.repaint();

        if (!last) {
            return;
        }

        this.state = WindowState.END;
        if (this.uncover != null) {
            this.uncover.setVisible(false);
        }
        this.setTitle("ゲーム - 終了 " + this.client.getAddresses());
        this.addCompForEnd();
    }

    private void addCompForEnd() {
        if (this.gamePanel != null) {
            this.gamePanel.setVisible(false);
            this.remove(this.gamePanel);
        }
        if (this.restart != null) {
            this.restart.setVisible(false);
        }

        var image = new ImageLabel("/zero.jpg");
        image.setMaximumSize(new Dimension(Constants.CARD_WIDTH, Integer.MAX_VALUE));
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(image);
        p.add(new JScrollPane(this.list));
        var l = new GridBagLayout();
        this.gamePanel = new JPanel(l);
        if (this.amIHost()) {
            this.restart = new JButton("もう一度遊ぶ");
            this.restart.setActionCommand("restart");
            this.restart.addActionListener(this);
            addButton(this.gamePanel, p, l, 0, 0, 1, 1, 1.0D);
            addButton(this.gamePanel, this.restart, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(this.gamePanel, p, l, 0, 0, 1, 1, 1.0D);
        }

        this.add(this.gamePanel, BorderLayout.CENTER);
        this.revalidate();
    }

    private void restart() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.RESTART));
    }

    public void reset() {
        this.state = WindowState.LOBBY;
        this.remove(this.startGame);
        this.startGame = null;
        this.remove(this.lobbyPanel);
        this.lobbyPanel = null;
        this.remove(this.cardPanel);
        this.cardPanel = null;
        this.remove(this.card);
        this.card = null;
        this.remove(this.cardNum);
        this.cardNum = null;
        this.remove(this.show);
        this.show = null;
        this.remove(this.ack);
        this.ack = null;
        this.remove(this.waitHost);
        this.waitHost = null;
        this.remove(this.selectTopic);
        this.selectTopic = null;
        this.topic = null;
        this.remove(this.topicPanel);
        this.topicPanel = null;
        this.remove(this.decideTopic);
        this.decideTopic = null;
        this.remove(this.list);
        this.list = null;
        this.remove(this.finish);
        this.finish = null;
        this.remove(this.gamePanel);
        this.gamePanel = null;
        this.remove(this.uncover);
        this.uncover = null;
        this.remove(this.restart);
        this.restart = null;
        this.repaint();
        this.revalidate();
        this.createSouth();
    }

    @Override
    public void remove(@Nullable Component comp) {
        if (comp != null) {
            comp.setVisible(false);
            super.remove(comp);
        }
    }

    @Nullable
    @Override
    protected JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        var menu = new JMenu("メニュー");

        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);

        if (this.state != WindowState.LOBBY) {
            var exit = new JMenuItem("ゲームをやめる");
            exit.setActionCommand("exit");
            exit.addActionListener(this);
            menu.add(exit);
        }

        menu.add(disconnect);
        jMenuBar.add(menu);

        var debug = new JMenu("ネットワーク");
        if (this.packetLog == null) {
            this.packetLog = new JMenuItem("ログを見る");
            this.packetLog.setActionCommand("packetLog");
            this.packetLog.addActionListener(this);
        }
        debug.add(this.packetLog);
        jMenuBar.add(debug);

        return jMenuBar;
    }

    private void startGame() {
        this.client.getConnection().sendPacket(new StartGameC2SPacket());
    }

    private void exitGame() {
        if (this.state == WindowState.LOBBY) {
            return;
        }

        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.EXIT));
    }

    public void onChangeHost() {
        switch (this.state) {
            case LOBBY -> this.lobby();
            case WAITING_HOST -> this.ackCardPost();
            case SELECTING_TOPIC -> this.addCompForTopic();
            case PLAYING -> this.addCompForPlay();
            case RESULT -> this.addCompForResult();
            case END -> this.addCompForEnd();
        }
    }

    public void onSpiderLeft(NumberCard card) {
        if (this.list != null) {
            this.list.removeCard(card);
        }
    }

    private boolean amIHost() {
        return this.client.clientSpider != null && this.client.listener != null && this.client.clientSpider.getId() == this.client.listener.getHostId();
    }

    @Override
    protected void onClose() {
        this.client.disconnect();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        var c = e.getComponent();
        if (c == this && this.south != null) {
            this.south.setPreferredSize(new Dimension(100, c.getHeight() / 4));
            this.south.revalidate();
        }
        if (c == this) {
            this.east.setPreferredSize(new Dimension(c.getWidth() / 3, c.getHeight() / 2));
            this.east.revalidate();
        }
    }

    private synchronized void showPacketLog() {
        this.add(this.east, BorderLayout.EAST);
        this.packetLog.setText("ログを閉じる");
        this.packetLog.setActionCommand("hPacketLog");
        this.revalidate();
    }

    private synchronized void hidePacketLog() {
        super.remove(this.east);
        this.packetLog.setText("ログを見る");
        this.packetLog.setActionCommand("packetLog");
        this.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start":
                this.startGame();
                break;
            case "disconnect":
                this.onClose();
                break;
            case "packetLog":
                this.showPacketLog();
                break;
            case "hPacketLog":
                this.hidePacketLog();
                break;
            case "show":
                this.showCard();
                break;
            case "ack":
                this.ackCard();
                break;
            case "select":
                this.startSelectingTopic();
                break;
            case "reselect":
                this.reselect();
                break;
            case "decide":
                this.decideTopic();
                break;
            case "finish":
                this.finish();
                break;
            case "uncover":
                this.uncover();
                break;
            case "restart":
                this.restart();
                break;
            case "exit":
                this.exitGame();
                break;
        }
    }

    private enum WindowState {
        LOBBY,
        STARTED,
        WAITING_HOST,
        SELECTING_TOPIC,
        PLAYING,
        RESULT,
        END
    }
}
