package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.panel.ImagePanel;
import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel;
import com.hamusuke.threadr.client.gui.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.hamusuke.threadr.client.gui.window.Window.addButton;

public class HandedCardPanel extends AbstractMainPanel {
    public HandedCardPanel(MainWindow parent) {
        super(parent);
    }

    @Override
    protected String getTitle() {
        return "ゲーム - 配られたカードの数字を確認 " + this.client.getAddresses();
    }

    @Override
    protected JPanel createCenter() {
        var card = this.addCenterComponent(new ImagePanel("/card.jpg", true));
        card.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var show = this.addCenterComponent(new JButton("数字を見る"));
        show.setActionCommand("show");
        show.addActionListener(this);
        var layout = new GridBagLayout();
        var p = new JPanel(layout);
        addButton(p, card, layout, 0, 0, 1, 1, 1.0D);
        addButton(p, show, layout, 0, 1, 1, 1, 0.125D);

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.parent.changeState(PanelState.CHECKING_NUMBER);
        this.client.spiderTable.addCardNumCol();
    }
}
