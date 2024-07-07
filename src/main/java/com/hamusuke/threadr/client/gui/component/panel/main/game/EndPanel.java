package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EndPanel extends Panel {
    public EndPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - 終了 " + this.client.getGameTitle());
        var p = this.createGamePanel();
        var l = (GridBagLayout) this.getLayout();
        this.placeComponents(p, l);
    }

    protected void placeComponents(JPanel p, GridBagLayout l) {
        addButton(this, p, l, 0, 0, 1, 1, 1.0D);

        if (this.client.amIHost()) {
            var restart = new JButton("もう一度遊ぶ");
            restart.addActionListener(this);
            addButton(this, restart, l, 0, 1, 1, 1, 0.125D);
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
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.RESTART));
    }
}
