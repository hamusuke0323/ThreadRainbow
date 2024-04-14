package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.client.gui.component.panel.misc.ImagePanel;

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

        this.client.setWindowTitle("ゲーム - 配られたカードの数字を確認 " + this.client.getGameTitle());
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
        jMenuBar.add(this.createMenuMenu());
        jMenuBar.add(this.createChatMenu());
        jMenuBar.add(this.createNetworkMenu());
        return jMenuBar;
    }

    @Override
    protected JMenu createMenuMenu() {
        var menu = super.createMenuMenu();
        var exit = new JMenuItem("ゲームをやめる");
        exit.setActionCommand("exit");
        exit.addActionListener(this.client.getMainWindow());
        var leave = new JMenuItem("部屋から退出");
        leave.setActionCommand("leave");
        leave.addActionListener(this.client.getMainWindow());
        menu.insert(exit, 0);
        menu.insert(leave, 1);
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.setPanel(new CheckingNumberPanel());
        SwingUtilities.invokeLater(this.client.spiderTable::addCardNumCol);
    }
}
