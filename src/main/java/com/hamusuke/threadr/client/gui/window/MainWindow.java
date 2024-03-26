package com.hamusuke.threadr.client.gui.window;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.ImageLabel;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.LocalCard;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.card.RemoteCard;
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
import java.util.List;

public class MainWindow extends Window {
    private static final Logger LOGGER = LogManager.getLogger();
    private JPanel south;
    private WindowState state = WindowState.NONE;
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
    private JPanel listPanel;
    private JButton finish;
    private JPanel gamePanel;

    public MainWindow() {
        super("ロビー");
    }

    @Override
    public void init() {
        super.init();

        this.setTitle("ロビー - " + this.client.getAddresses());
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

        this.add(this.south, BorderLayout.SOUTH);
        this.revalidate();
    }

    public void lobby() {
        if (this.lobbyPanel != null) {
            return;
        }

        this.client.spiderTable.removeCardNumCol();
        this.startGame = new JButton("始める");
        this.startGame.setActionCommand("start");
        this.startGame.addActionListener(this);
        this.startGame.setVisible(false);
        this.startGame.setEnabled(false);
        this.lobbyPanel = new JPanel(new FlowLayout());
        this.lobbyPanel.add(this.startGame);
        this.add(this.lobbyPanel, BorderLayout.CENTER);
        this.revalidate();
    }

    public void rmLobby() {
        this.lobbyPanel.setVisible(false);
        this.lobbyPanel.setEnabled(false);
        this.remove(this.lobbyPanel);
        this.startGame = null;
        this.lobbyPanel = null;
        this.revalidate();
    }

    public void card() {
        if (this.cardPanel != null) {
            return;
        }

        this.setTitle("ゲーム - 配られたカードの数字を確認 " + this.client.getAddresses());
        this.card = new ImageLabel("/card.jpg");
        this.card.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        this.cardNum = new JLabel(this.client.clientSpider.getLocalCard().num() + "", SwingConstants.CENTER);
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

    public void lineupCard() {
        this.rmTopic();
        this.state = WindowState.PLAYING;
        this.setTitle("ゲーム - 「たとえ」て小さい順に並べる " + this.client.getAddresses());
        this.createSouth();
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
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        if (this.list == null) {
            this.list = new NumberCardList();
            var model = new DefaultListModel<NumberCard>();
            model.addElement(new LocalCard(new LocalSpider("あああ"), (byte) 8));
            model.addAll(Lists.newArrayList(1, 5, 10, 15, 22, 24).stream().map(integer -> new RemoteCard(new RemoteSpider("nanashi" + integer))).toList());
            this.list.setModel(model);
        }

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

    private void finish() {
        this.client.getConnection().sendPacket(new ClientCommandC2SPacket(Command.FINISH));
    }

    @Nullable
    @Override
    protected JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        var menu = new JMenu("Menu");
        var disconnect = new JMenuItem("Disconnect");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        menu.add(disconnect);
        jMenuBar.add(menu);
        return jMenuBar;
    }

    private void startGame() {
        this.client.getConnection().sendPacket(new StartGameC2SPacket());
    }

    public void onChangeHost() {
        switch (this.state) {
            case WAITING_HOST -> this.ackCardPost();
            case SELECTING_TOPIC -> this.addCompForTopic();
            case PLAYING -> this.addCompForPlay();
        }
    }

    private boolean amIHost() {
        return this.client.clientSpider != null && this.client.listener != null && this.client.clientSpider.getId() == this.client.listener.getHostId();
    }

    public void showStartButton(boolean flag) {
        this.startGame.setVisible(flag);
        this.startGame.setEnabled(flag);
    }

    @Override
    protected void onClose() {
        this.dispose(this.client::disconnect);
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
        }
    }

    private enum WindowState {
        NONE,
        WAITING_HOST,
        SELECTING_TOPIC,
        PLAYING,
        RESULT
    }
}
