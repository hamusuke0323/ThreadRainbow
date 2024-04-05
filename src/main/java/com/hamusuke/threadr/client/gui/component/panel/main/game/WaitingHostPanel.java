package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WaitingHostPanel extends Panel {
    public WaitingHostPanel() {
        super(new FlowLayout());
    }

    @Override
    public void init() {
        super.init();

        if (this.client.amIHost()) {
            var selectTopic = new JButton("お題を選ぶ");
            selectTopic.setActionCommand("select");
            selectTopic.addActionListener(this);
            this.add(selectTopic);
        } else {
            var waitHost = new JLabel("ホストが次に進むのを待っています...", SwingConstants.CENTER);
            this.add(waitHost);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.START_TOPIC_SELECTION));
    }
}
