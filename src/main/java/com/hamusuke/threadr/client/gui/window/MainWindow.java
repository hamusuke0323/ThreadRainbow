package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.network.protocol.packet.c2s.lobby.StartGameC2SPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainWindow extends Window {
    private static final Logger LOGGER = LogManager.getLogger();
    private JButton startGame;
    private JPanel lobbyPanel;

    public MainWindow() {
        super("ロビー");
    }

    private String getWindowTitle() {
        return "ロビー - " + this.client.getAddresses();
    }

    @Override
    public void init() {
        super.init();

        this.setTitle(this.getWindowTitle());
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

        this.startGame = new JButton("始める");
        this.startGame.setActionCommand("start");
        this.startGame.addActionListener(this);
        this.startGame.setVisible(false);
        this.startGame.setEnabled(false);
        this.lobbyPanel = new JPanel(new FlowLayout());
        this.lobbyPanel.add(this.startGame);
        this.add(this.lobbyPanel, BorderLayout.CENTER);
    }

    public void rmLobby() {
        this.lobbyPanel.setVisible(false);
        this.lobbyPanel.setEnabled(false);
        this.remove(this.lobbyPanel);
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
        }
    }
}
