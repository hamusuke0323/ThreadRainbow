package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WaitingHostPanel extends Panel {
    private final String buttonText;
    private final Command command;

    public WaitingHostPanel() {
        this("お題を選ぶ", Command.START_TOPIC_SELECTION);
    }

    public WaitingHostPanel(String buttonText, Command command) {
        super(new FlowLayout());
        this.buttonText = buttonText;
        this.command = command;
    }

    @Override
    public void init() {
        super.init();

        if (this.client.amIHost()) {
            var selectTopic = new JButton(this.buttonText);
            selectTopic.addActionListener(this);
            this.add(selectTopic);
        } else {
            var waitHost = new JLabel("ホストが次に進むのを待っています...", SwingConstants.CENTER);
            this.add(waitHost);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(this.command));
    }
}
