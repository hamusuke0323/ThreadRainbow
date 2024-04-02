package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SelectingTopicPanel extends AbstractMainPanel {
    public SelectingTopicPanel(MainWindow parent) {
        super(parent);
        this.client.spiderTable.addCardNumCol();
    }

    @Override
    protected String getTitle() {
        return "ゲーム - お題決定 " + this.client.getAddresses();
    }

    @Override
    protected JPanel createCenter() {
        JPanel p;

        if (this.amIHost()) {
            var selectTopic = this.addCenterComponent(new JButton("もう一度選ぶ"));
            selectTopic.setActionCommand("change");
            selectTopic.addActionListener(this);
            var decideTopic = this.addCenterComponent(new JButton("決定"));
            decideTopic.setActionCommand("decide");
            decideTopic.addActionListener(this);
            p = this.parent.getTopic().toPanel(List.of(selectTopic, decideTopic));
        } else {
            p = this.parent.getTopic().toPanel();
        }

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "change" -> this.client.getConnection().sendPacket(new ClientCommandReq(Command.CHANGE_TOPIC));
            case "decide" -> this.client.getConnection().sendPacket(new ClientCommandReq(Command.DECIDE_TOPIC));
        }
    }
}
