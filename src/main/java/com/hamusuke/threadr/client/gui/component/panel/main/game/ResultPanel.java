package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResultPanel extends Panel {
    public ResultPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - 結果発表 " + this.client.getGameTitle());
        var p = this.createGamePanel();
        var l = (GridBagLayout) this.getLayout();
        this.placeComponents(p, l);
    }

    protected void placeComponents(JPanel p, GridBagLayout l) {
        if (this.client.amIHost()) {
            var uncover = new JButton("カードをめくる");
            uncover.setActionCommand("uncover");
            uncover.addActionListener(this);
            addButton(this, p, l, 0, 0, 1, 1, 1.0D);
            addButton(this, uncover, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(this, p, l, 0, 0, 1, 1, 1.0D);
        }
    }

    @Override
    protected NumberCardList createNumberCardList() {
        var list = NumberCardList.result(this.client);
        list.setModel(this.client.model);
        return list;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.UNCOVER));
    }
}
