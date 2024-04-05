package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.component.panel.ImagePanel;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResultPanel extends PlayingPanel {
    public ResultPanel(Topic topic) {
        super(topic);
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - 結果発表 " + this.client.getAddresses());
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
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.UNCOVER));
    }
}
