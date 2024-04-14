package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.client.gui.component.panel.misc.ImagePanel;
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
        var image = new ImagePanel("/zero.jpg");
        image.setMaximumSize(new Dimension(Constants.CARD_WIDTH, Integer.MAX_VALUE));
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(image);
        var list = NumberCardList.result(this.client);
        list.setModel(this.client.model);
        p.add(new JScrollPane(list));
        var l = (GridBagLayout) this.getLayout();
        if (this.client.amIHost()) {
            var restart = new JButton("もう一度遊ぶ");
            restart.setActionCommand("restart");
            restart.addActionListener(this);
            addButton(this, p, l, 0, 0, 1, 1, 1.0D);
            addButton(this, restart, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(this, p, l, 0, 0, 1, 1, 1.0D);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.RESTART));
    }
}
