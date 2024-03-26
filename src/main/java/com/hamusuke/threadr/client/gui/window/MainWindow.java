package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.ImageLabel;
import com.hamusuke.threadr.network.protocol.packet.c2s.lobby.StartGameC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket;
import com.hamusuke.threadr.network.protocol.packet.c2s.play.ClientCommandC2SPacket.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainWindow extends Window {
    private static final Logger LOGGER = LogManager.getLogger();
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

    public MainWindow() {
        super("ロビー");
    }

    @Override
    public void init() {
        super.init();

        this.setTitle("ロビー - " + this.client.getAddresses());
        var layout = new GridBagLayout();
        var panel = new JPanel(layout);
        addButton(panel, this.client.chat.getTextArea(), layout, 0, 0, 1, 1, 1.0D);
        addButton(panel, this.client.chat.getField(), layout, 0, 1, 1, 1, 0.125D);
        this.add(panel, BorderLayout.SOUTH);
        this.add(new JScrollPane(this.client.spiderTable), BorderLayout.EAST);
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);
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

    public void topic() {
        this.setTitle("ゲーム - お題決定 " + this.client.getAddresses());
    }

    public void rmTopic() {

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
            case "topic":
                this.startSelectingTopic();
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
