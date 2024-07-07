package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.game.card.NumberCard;

import javax.swing.*;
import java.awt.*;

public class TeamResultPanel extends ResultPanel {
    protected final boolean isMyTeam;
    private final DefaultListModel<NumberCard> teamCards;

    public TeamResultPanel(DefaultListModel<NumberCard> teamCards, boolean isMyTeam) {
        this.teamCards = teamCards;
        this.isMyTeam = isMyTeam;
    }

    @Override
    protected void placeComponents(JPanel p, GridBagLayout l) {
        addButton(this, new JLabel(this.isMyTeam ? "自分のチーム" : "相手のチーム", SwingConstants.CENTER), l, 0, 0, 1, 1, 0.0125D);
        addButton(this, p, l, 0, 1, 1, 1, 1.0D);
        if (this.client.amIHost()) {
            var uncover = new JButton("カードをめくる");
            uncover.addActionListener(this);
            addButton(this, uncover, l, 0, 2, 1, 1, 0.125D);
        }
    }

    @Override
    protected NumberCardList createNumberCardList() {
        var list = NumberCardList.result(this.client);
        list.setModel(this.teamCards);
        return list;
    }
}
