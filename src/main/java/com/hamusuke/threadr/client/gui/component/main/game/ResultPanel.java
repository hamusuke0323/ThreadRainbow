package com.hamusuke.threadr.client.gui.component.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.ImageLabel;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.hamusuke.threadr.client.gui.window.Window.addButton;

public class ResultPanel extends PlayingPanel {
    public ResultPanel(MainWindow parent) {
        super(parent);
    }

    @Override
    protected String getTitle() {
        return "ゲーム - 結果発表 " + this.client.getAddresses();
    }

    @Override
    protected JPanel createCenter() {
        var image = this.addCenterComponent(new ImageLabel("/zero.jpg"));
        image.setMaximumSize(new Dimension(Constants.CARD_WIDTH, Integer.MAX_VALUE));
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var p = this.addCenterComponent(new JPanel());
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(image);
        var list = NumberCardList.result(this.client);
        list.setModel(this.parent.getModel());
        p.add(this.addCenterComponent(new JScrollPane(list)));
        var l = new GridBagLayout();
        var gamePanel = new JPanel(l);
        if (this.amIHost()) {
            var uncover = this.addCenterComponent(new JButton("カードをめくる"));
            uncover.setActionCommand("uncover");
            uncover.addActionListener(this);
            addButton(gamePanel, p, l, 0, 0, 1, 1, 1.0D);
            addButton(gamePanel, uncover, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(gamePanel, p, l, 0, 0, 1, 1, 1.0D);
        }
        return gamePanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.UNCOVER));
    }
}
