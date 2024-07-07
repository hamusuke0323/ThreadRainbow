package com.hamusuke.threadr.client.gui.component.panel.main.room;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.game.mode.GameMode;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.SelectGameModeReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.StartGameReq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RoomPanel extends Panel implements ItemListener {
    private final JComboBox<GameMode> gameModeBox;
    private final JLabel gameModeLabel;
    private final AtomicBoolean dontSendPacket = new AtomicBoolean();

    public RoomPanel() {
        super(new GridLayout(0, 1));

        this.gameModeBox = new JComboBox<>(GameMode.values());
        this.gameModeBox.setSelectedItem(GameMode.SPIDERS_THREAD_V2);
        this.gameModeBox.addItemListener(this);

        this.gameModeLabel = new JLabel(this.gameModeBox.getSelectedItem().toString());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle(Objects.requireNonNull(this.client.curRoom).getRoomName() + " - " + this.client.getGameTitle());
        SwingUtilities.invokeLater(this.client.spiderTable::removeCardNumCol);
        SwingUtilities.invokeLater(() -> this.client.spiderTable.setOverrideRenderer(null));

        synchronized (this.client.curRoom.getSpiders()) {
            this.client.curRoom.getSpiders().forEach(spider -> {
                spider.setClientCard(null);
                spider.myTeam = null;
            });
        }

        var gm = new JLabel("ゲームモード");
        gm.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(gm);
        if (this.client.amIHost()) {
            this.add(this.gameModeBox);
            var startGame = new JButton("始める");
            startGame.setActionCommand("start");
            startGame.addActionListener(this);
            this.add(startGame);
        } else {
            this.add(this.gameModeLabel);
            var l = new JLabel("ホストがゲームを始めるまでお待ちください");
            l.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(l);
        }
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        jMenuBar.add(this.createMenuMenu());
        jMenuBar.add(this.createChatMenu());
        jMenuBar.add(this.createNetworkMenu());
        jMenuBar.add(this.createTopicMenu());
        return jMenuBar;
    }

    @Override
    protected JMenu createMenuMenu() {
        var m = super.createMenuMenu();
        var leave = new JMenuItem("部屋から退出");
        leave.setActionCommand("leave");
        leave.addActionListener(this.client.getMainWindow());
        m.insert(leave, 0);
        return m;
    }

    @Override
    public JPanel createSouth() {
        var layout = new GridBagLayout();
        var south = new JPanel(layout);
        var chatPanel = this.createChatPanel();
        var table = this.createTable();
        addButton(south, chatPanel, layout, 0, 0, 2, 1, 1.0D);
        addButton(south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        return south;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new StartGameReq());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (this.dontSendPacket.get() || !this.client.amIHost() || e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        this.client.getConnection().sendPacket(new SelectGameModeReq((GameMode) this.gameModeBox.getSelectedItem()));
    }

    public void setSelectedItem(GameMode gameMode) {
        this.dontSendPacket.set(true);
        this.gameModeBox.setSelectedItem(gameMode);
        this.gameModeLabel.setText(gameMode.toString());
        this.dontSendPacket.set(false);
        this.revalidate();
    }
}
