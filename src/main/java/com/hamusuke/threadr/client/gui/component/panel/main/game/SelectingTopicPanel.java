package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SelectingTopicPanel extends Panel {
    private final Topic topic;

    public SelectingTopicPanel(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - お題決定 " + this.client.getGameTitle());

        if (this.client.amIHost()) {
            var selectTopic = new JButton("もう一度選ぶ");
            selectTopic.setActionCommand("change");
            selectTopic.addActionListener(this);
            var decideTopic = new JButton("決定");
            decideTopic.setActionCommand("decide");
            decideTopic.addActionListener(this);
            this.add(this.topic.toPanel(List.of(selectTopic, decideTopic)));
        } else {
            this.add(this.topic.toPanel());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "change" -> this.client.getConnection().sendPacket(new ClientCommandReq(Command.CHANGE_TOPIC));
            case "decide" -> this.client.getConnection().sendPacket(new ClientCommandReq(Command.DECIDE_TOPIC));
        }
    }
}
