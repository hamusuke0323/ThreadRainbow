package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel;
import com.hamusuke.threadr.client.gui.component.panel.main.AbstractMainPanel.PanelState;
import com.hamusuke.threadr.client.network.listener.main.ClientPlayPacketListenerImpl;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.List;

public class MainWindow extends Window {
    private static final Logger LOGGER = LogManager.getLogger();
    private PanelState state;
    private Topic topic;
    private JPanel east;
    private JMenuItem packetLog;
    @Nullable
    private AbstractMainPanel mainPanel;
    private DefaultListModel<NumberCard> model;

    public MainWindow() {
        super("ThreadRainbow");
    }

    @Override
    public void init() {
        super.init();

        this.east = new JPanel(new FlowLayout());
        var logScroll = new JScrollPane(this.client.packetLogTable);
        logScroll.setAutoscrolls(true);
        this.east.add(logScroll);
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);
    }

    public void changeState(PanelState state) {
        this.state = state;
        if (this.mainPanel != null) {
            this.mainPanel.destroyAll();
        }

        this.onStateChanged(state);
        this.mainPanel = state.create(this);
    }

    private void onStateChanged(PanelState newState) {
        switch (newState) {
            case LOBBY, HANDED_CARD -> {
                if (this.menu != null) {
                    this.menu.setVisible(false);
                    this.remove(this.menu);
                }
                this.menu = this.createMenuBar();
                this.add(this.menu, BorderLayout.NORTH);
            }
        }
    }

    public Topic getTopic() {
        return this.topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
        this.changeState(PanelState.SELECTING_TOPIC);
    }

    public void lineupCard(List<Integer> cards) {
        this.model = new DefaultListModel<>();
        cards.forEach(i -> {
            var card = ((ClientPlayPacketListenerImpl) this.client.listener).getCardById(i);
            if (card == null) {
                LOGGER.warn("null card returned! should never happen.");
                return;
            }

            this.model.addElement(card);
        });

        this.changeState(PanelState.PLAYING);
    }

    public DefaultListModel<NumberCard> getModel() {
        return this.model;
    }

    public void onCardMoved(int from, int to) {
        var str = this.model.get(from);
        if (to < from) {
            this.model.remove(from);
            this.model.add(to, str);
        } else {
            this.model.add(to + 1, str);
            this.model.remove(from);
        }

        this.repaint();
    }

    public void onUncovered(int id, byte number, boolean last) {
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.stream().filter(s -> s.getId() == id).findFirst().ifPresent(s -> {
                if (s instanceof RemoteSpider r) {
                    r.getRemoteCard().setNumber(number);
                    r.getRemoteCard().uncover();
                } else if (s instanceof LocalSpider l) {
                    l.getLocalCard().uncover();
                }
            });
        }

        this.repaint();

        if (!last) {
            return;
        }

        this.changeState(PanelState.END);
    }

    @Nullable
    @Override
    protected JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        var menu = new JMenu("メニュー");

        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);

        if (this.state != PanelState.LOBBY) {
            var exit = new JMenuItem("ゲームをやめる");
            exit.setActionCommand("exit");
            exit.addActionListener(this);
            menu.add(exit);
        }

        menu.add(disconnect);
        jMenuBar.add(menu);

        var debug = new JMenu("ネットワーク");
        if (this.packetLog == null) {
            this.packetLog = new JMenuItem("ログを見る");
            this.packetLog.setActionCommand("packetLog");
            this.packetLog.addActionListener(this);
        }
        debug.add(this.packetLog);
        jMenuBar.add(debug);

        return jMenuBar;
    }

    private void exitGame() {
        if (this.state == PanelState.LOBBY) {
            return;
        }

        this.client.getConnection().sendPacket(new ClientCommandReq(Command.EXIT));
    }

    public void onChangeHost() {
        if (this.mainPanel != null) {
            this.mainPanel.onRoleChanged();
        }
    }

    public void onSpiderLeft(NumberCard card) {
        if (this.model != null) {
            this.model.removeElement(card);
            this.repaint();
        }
    }

    @Override
    protected void onClose() {
        this.client.disconnect();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        var c = e.getComponent();
        if (c == this) {
            this.east.setPreferredSize(new Dimension(c.getWidth() / 3, c.getHeight() / 2));
            this.east.revalidate();
        }

        if (this.mainPanel != null) {
            this.mainPanel.componentResized(e);
        }
    }

    private synchronized void showPacketLog() {
        this.add(this.east, BorderLayout.EAST);
        this.packetLog.setText("ログを閉じる");
        this.packetLog.setActionCommand("hPacketLog");
        this.revalidate();
    }

    private synchronized void hidePacketLog() {
        this.remove(this.east);
        this.packetLog.setText("ログを見る");
        this.packetLog.setActionCommand("packetLog");
        this.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "disconnect":
                this.onClose();
                break;
            case "packetLog":
                this.showPacketLog();
                break;
            case "hPacketLog":
                this.hidePacketLog();
                break;
            case "exit":
                this.exitGame();
                break;
        }
    }
}
