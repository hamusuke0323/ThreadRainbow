package com.hamusuke.threadr.client.gui.component.panel.main.room;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.room.StartGameReq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RoomPanel extends Panel {
    public RoomPanel() {
        super(new FlowLayout());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ロビー - " + this.client.getAddresses());
        SwingUtilities.invokeLater(this.client.spiderTable::removeCardNumCol);
        if (this.client.amIHost()) {
            var startGame = new JButton("始める");
            startGame.setActionCommand("start");
            startGame.addActionListener(this);
            this.add(startGame);
        } else {
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
}
