package com.hamusuke.threadr.client.gui.component.main;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.main.game.*;
import com.hamusuke.threadr.client.gui.component.main.lobby.LobbyPanel;
import com.hamusuke.threadr.client.gui.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.function.Function;

import static com.hamusuke.threadr.client.gui.window.Window.addButton;

public abstract class AbstractMainPanel implements ActionListener, ComponentListener {
    protected final MainWindow parent;
    protected final ThreadRainbowClient client;
    protected final List<JComponent> centerComponents = Lists.newArrayList();
    protected final List<JComponent> southComponents = Lists.newArrayList();
    private JPanel south;

    protected AbstractMainPanel(MainWindow parent) {
        this.parent = parent;
        this.client = ThreadRainbowClient.getInstance();
        this.addCenter();
        this.addSouth();
        this.parent.setTitle(this.getTitle());
    }

    protected <C extends JComponent> C addCenterComponent(C component) {
        this.centerComponents.add(component);
        return component;
    }

    protected <C extends JComponent> C addSouthComponent(C component) {
        this.southComponents.add(component);
        return component;
    }

    protected abstract String getTitle();

    public void destroyAll() {
        this.destroyCenter();
        this.destroySouth();
    }

    protected void addCenter() {
        this.destroyCenter();
        this.parent.add(this.addCenterComponent(this.createCenter()), BorderLayout.CENTER);
        this.parent.revalidate();
    }

    protected abstract JPanel createCenter();

    protected void destroyCenter() {
        this.centerComponents.forEach(c -> {
            c.setEnabled(false);
            c.setVisible(false);
            this.parent.remove(c);
        });
        this.centerComponents.clear();
        this.parent.revalidate();
    }

    protected void addSouth() {
        this.destroySouth();
        this.south = this.addSouthComponent(this.createSouth());
        this.parent.add(this.south, BorderLayout.SOUTH);
        this.parent.revalidate();
    }

    protected JPanel createSouth() {
        var layout = new GridBagLayout();
        var south = new JPanel(layout);
        var chatPanel = this.addSouthComponent(this.createChatPanel());
        var table = this.addSouthComponent(this.createTable());
        addButton(south, chatPanel, layout, 0, 0, 2, 1, 1.0D);
        addButton(south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        south.setPreferredSize(new Dimension(100, this.parent.getHeight() / 4));
        return south;
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

    protected void destroySouth() {
        this.southComponents.forEach(c -> {
            c.setEnabled(false);
            c.setVisible(false);
            this.parent.remove(c);
        });
        this.southComponents.clear();
        this.parent.revalidate();
    }

    public void onRoleChanged() {
        this.addCenter();
    }

    protected boolean amIHost() {
        return this.client.listener != null && this.client.clientSpider != null && this.client.listener.getHostId() == this.client.clientSpider.getId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        var c = e.getComponent();
        if (c == this.parent && this.south != null) {
            this.south.setPreferredSize(new Dimension(100, c.getHeight() / 4));
            this.south.revalidate();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    public enum PanelState {
        LOBBY(LobbyPanel::new),
        HANDED_CARD(HandedCardPanel::new),
        CHECKING_NUMBER(CheckingNumberPanel::new),
        WAITING_HOST(WaitingHostPanel::new),
        SELECTING_TOPIC(SelectingTopicPanel::new),
        PLAYING(PlayingPanel::new),
        RESULT(ResultPanel::new),
        END(EndPanel::new);

        private final Function<MainWindow, AbstractMainPanel> panelFactory;

        PanelState(Function<MainWindow, AbstractMainPanel> panelFactory) {
            this.panelFactory = panelFactory;
        }

        public AbstractMainPanel create(MainWindow parent) {
            return this.panelFactory.apply(parent);
        }
    }
}
