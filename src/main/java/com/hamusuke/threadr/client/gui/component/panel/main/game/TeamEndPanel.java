package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TeamEndPanel extends EndPanel {
    @Override
    protected void placeComponents(JPanel p, GridBagLayout l) {
        addButton(this, p, l, 0, 0, 1, 1, 1.0D);

        if (this.client.amIHost()) {
            var restart = new JButton("もう一度遊ぶ");
            restart.setActionCommand("restart");
            restart.addActionListener(this);
            var restartWithSameTeam = new JButton("チームを変えずにもう一度遊ぶ");
            restartWithSameTeam.setActionCommand("restartWithSameTeam");
            restartWithSameTeam.addActionListener(this);
            addButton(this, restart, l, 0, 1, 1, 1, 0.125D);
            addButton(this, restartWithSameTeam, l, 0, 2, 1, 1, 0.125D);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "restart":
                this.client.getConnection().sendPacket(new ClientCommandReq(Command.RESTART));
                break;
            case "restartWithSameTeam":
                this.client.getConnection().sendPacket(new ClientCommandReq(Command.RESTART_WITH_THE_SAME_TEAM));
                break;
        }
    }
}
