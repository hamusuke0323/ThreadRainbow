package com.hamusuke.threadr.client.gui.component.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.main.AbstractMainPanel;
import com.hamusuke.threadr.client.gui.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.hamusuke.threadr.client.gui.window.Window.addButton;

public class CheckingNumberPanel extends AbstractMainPanel {
    public CheckingNumberPanel(MainWindow parent) {
        super(parent);
    }

    @Override
    protected String getTitle() {
        return "ゲーム - 配られたカードの数字を確認 " + this.client.getAddresses();
    }

    @Override
    protected JPanel createCenter() {
        var cardNum = this.addCenterComponent(new JLabel(this.client.clientSpider.getLocalCard().getNumber() + "", SwingConstants.CENTER));
        cardNum.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));

        var ack = this.addCenterComponent(new JButton("OK!"));
        ack.setActionCommand("ack");
        ack.addActionListener(this);
        var layout = new GridBagLayout();
        var cardPanel = new JPanel(layout);
        addButton(cardPanel, cardNum, layout, 0, 0, 1, 1, 1.0D);
        addButton(cardPanel, ack, layout, 0, 1, 1, 1, 0.125D);

        return cardPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.parent.changeState(PanelState.WAITING_HOST);
    }
}
