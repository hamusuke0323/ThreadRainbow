package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TeamNextResultPanel extends TeamResultPanel {
    public TeamNextResultPanel(DefaultListModel<NumberCard> teamCards, boolean isMyTeam) {
        super(teamCards, isMyTeam);
    }

    @Override
    protected void placeComponents(JPanel p, GridBagLayout l) {
        addButton(this, new JLabel(this.isMyTeam ? "自分のチーム" : "相手のチーム", SwingConstants.CENTER), l, 0, 0, 1, 1, 0.0125D);
        addButton(this, p, l, 0, 1, 1, 1, 1.0D);
        if (this.client.amIHost()) {
            var next = new JButton("次へ");
            next.addActionListener(this);
            addButton(this, next, l, 0, 2, 1, 1, 0.125D);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.NEXT));
    }
}
