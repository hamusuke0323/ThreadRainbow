package com.hamusuke.threadr.client.gui.component.panel.main.lobby;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.StartGameReq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LobbyPanel extends Panel {
    public LobbyPanel() {
        super(new FlowLayout());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ロビー - " + this.client.getAddresses());
        this.client.spiderTable.removeCardNumCol();
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
        var menu = new JMenu("メニュー");

        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this.client.getMainWindow());
        menu.add(disconnect);
        jMenuBar.add(menu);

        var debug = new JMenu("ネットワーク");

        jMenuBar.add(debug);

        return jMenuBar;
    }

    @Override
    public JPanel createSouth() {
        var layout = new GridBagLayout();
        var south = new JPanel(layout);
        var chatPanel = this.createChatPanel();
        var table = this.createTable();
        addButton(south, chatPanel, layout, 0, 0, 2, 1, 1.0D);
        addButton(south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        south.setPreferredSize(new Dimension(100, this.getHeight() / 4));
        return south;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new StartGameReq());
    }
}
