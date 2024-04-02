package com.hamusuke.threadr.client.gui.component.panel.main.lobby;

import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.StartGameReq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LobbyPanel extends AbstractMainPanel {
    public LobbyPanel(MainWindow parent) {
        super(parent);
    }

    @Override
    protected String getTitle() {
        return "ロビー - " + this.client.getAddresses();
    }

    @Override
    protected JPanel createCenter() {
        this.client.spiderTable.removeCardNumCol();
        var p = new JPanel(new FlowLayout());

        if (this.amIHost()) {
            var startGame = this.addCenterComponent(new JButton("始める"));
            startGame.setActionCommand("start");
            startGame.addActionListener(this);
            p.add(startGame);
        } else {
            var l = this.addCenterComponent(new JLabel("ホストがゲームを始めるまでお待ちください"));
            l.setHorizontalAlignment(SwingConstants.CENTER);
            p.add(l);
        }

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new StartGameReq());
    }
}
