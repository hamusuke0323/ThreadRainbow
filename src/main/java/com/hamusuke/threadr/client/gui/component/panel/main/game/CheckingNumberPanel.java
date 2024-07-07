package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.panel.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CheckingNumberPanel extends Panel {
    public CheckingNumberPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        var cardNum = new JLabel(this.client.clientSpider.getClientCard().getNumber() + "", SwingConstants.CENTER);
        cardNum.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));

        var ack = new JButton("OK!");
        ack.setActionCommand("ack");
        ack.addActionListener(this);
        var layout = (GridBagLayout) this.getLayout();
        addButton(this, cardNum, layout, 0, 0, 1, 1, 1.0D);
        addButton(this, ack, layout, 0, 1, 1, 1, 0.125D);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.setPanel(new WaitingHostPanel());
    }
}
