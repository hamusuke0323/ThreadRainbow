package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.panel.ImagePanel;
import com.hamusuke.threadr.client.gui.component.panel.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HandedCardPanel extends Panel {
    public HandedCardPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - 配られたカードの数字を確認 " + this.client.getAddresses());
        var card = new ImagePanel("/card.jpg", true);
        card.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        var show = new JButton("数字を見る");
        show.setActionCommand("show");
        show.addActionListener(this);
        var layout = (GridBagLayout) this.getLayout();
        addButton(this, card, layout, 0, 0, 1, 1, 1.0D);
        addButton(this, show, layout, 0, 1, 1, 1, 0.125D);
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        var menu = new JMenu("メニュー");

        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this.client.getMainWindow());

        var exit = new JMenuItem("ゲームをやめる");
        exit.setActionCommand("exit");
        exit.addActionListener(this.client.getMainWindow());
        menu.add(exit);

        menu.add(disconnect);
        jMenuBar.add(menu);

        var debug = new JMenu("ネットワーク");
        /*
        if (this.packetLog == null) {
            this.packetLog = new JMenuItem("ログを見る");
            this.packetLog.setActionCommand("packetLog");
            this.packetLog.addActionListener(this);
        }
        debug.add(this.packetLog);

         */
        jMenuBar.add(debug);

        return jMenuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.setPanel(new CheckingNumberPanel());
        this.client.spiderTable.addCardNumCol();
    }
}
