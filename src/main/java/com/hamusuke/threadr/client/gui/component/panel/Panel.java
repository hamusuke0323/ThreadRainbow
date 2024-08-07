package com.hamusuke.threadr.client.gui.component.panel;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.component.panel.misc.ImagePanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class Panel extends JPanel implements ActionListener, ComponentListener {
    protected ThreadRainbowClient client;

    protected Panel() {
        this(new BorderLayout());
    }

    protected Panel(LayoutManager layout) {
        super(layout);
    }

    public static void addButton(Container owner, Component component, GridBagLayout layout, int x, int y, int w, int h, double wx, double wy) {
        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(1, 1, 1, 1);
        constraints.gridwidth = w;
        constraints.gridheight = h;
        constraints.weightx = wx;
        constraints.weighty = wy;
        layout.setConstraints(component, constraints);
        owner.add(component);
    }

    public static void addButton(Container owner, Component component, GridBagLayout layout, int x, int y, int w, int h, double wh) {
        addButton(owner, component, layout, x, y, w, h, 1.0D, wh);
    }

    public void init() {
        this.client = ThreadRainbowClient.getInstance();
        this.registerKeyboardAction(e -> this.onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void tick() {
    }

    protected ImagePanel createZeroCardImagePanel() {
        var image = new ImagePanel("/zero.jpg");
        image.setMaximumSize(new Dimension(Constants.CARD_WIDTH, Integer.MAX_VALUE));
        image.setPreferredSize(new Dimension(Constants.CARD_WIDTH, Constants.CARD_HEIGHT));
        return image;
    }

    protected JPanel createGamePanel() {
        var p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(this.createZeroCardImagePanel());
        p.add(new JScrollPane(this.createNumberCardList()));
        return p;
    }

    protected NumberCardList createNumberCardList() {
        var list = NumberCardList.play(this.client);
        list.setModel(this.client.model);
        return list;
    }

    @Nullable
    public JMenuBar createMenuBar() {
        return null;
    }

    protected JMenu createMenuMenu() {
        var menu = new JMenu("メニュー");
        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this.client.getMainWindow());
        menu.add(disconnect);
        return menu;
    }

    protected JMenu createChatMenu() {
        var chat = new JMenu("チャット");
        var clear = new JMenuItem("チャット欄をクリア");
        clear.setActionCommand("clearChat");
        clear.addActionListener(this.client.getMainWindow());
        chat.add(clear);
        return chat;
    }

    protected JMenu createNetworkMenu() {
        var debug = new JMenu("ネットワーク");
        debug.add(this.client.getMainWindow().packetLog);
        debug.add(this.client.getMainWindow().autoScroll);
        var clearPackets = new JMenuItem("ログをクリア");
        clearPackets.addActionListener(this.client.getMainWindow());
        clearPackets.setActionCommand("clearPackets");
        debug.add(clearPackets);
        return debug;
    }

    protected JMenu createTopicMenu() {
        var m = new JMenu("お題");
        m.add(this.client.getMainWindow().topicList);
        return m;
    }

    @Nullable
    public JPanel createSouth() {
        return null;
    }

    protected JPanel createChatPanel() {
        var chatBag = new GridBagLayout();
        var chatPanel = new JPanel(chatBag);
        addButton(chatPanel, this.client.chat.getTextArea(), chatBag, 0, 0, 1, 1, 1.0D);
        addButton(chatPanel, this.client.chat.getField(), chatBag, 0, 1, 1, 1, 0.125D);
        return chatPanel;
    }

    protected JScrollPane createTable() {
        return new JScrollPane(this.client.spiderTable);
    }

    public void onRemoved() {
        this.removeAll();
    }

    public void onClose() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }
}
