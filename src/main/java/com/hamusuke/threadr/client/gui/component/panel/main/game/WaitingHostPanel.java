package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WaitingHostPanel extends AbstractMainPanel {
    public WaitingHostPanel(MainWindow parent) {
        super(parent);
    }

    @Override
    protected String getTitle() {
        return "ゲーム - 配られたカードの数字を確認 " + this.client.getAddresses();
    }

    @Override
    protected JPanel createCenter() {
        var p = new JPanel(new FlowLayout());

        if (this.amIHost()) {
            var selectTopic = this.addCenterComponent(new JButton("お題を選ぶ"));
            selectTopic.setActionCommand("select");
            selectTopic.addActionListener(this);
            p.add(selectTopic);
        } else {
            var waitHost = new JLabel("ホストが次に進むのを待っています...", SwingConstants.CENTER);
            p.add(waitHost);
        }

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.START_TOPIC_SELECTION));
    }
}
