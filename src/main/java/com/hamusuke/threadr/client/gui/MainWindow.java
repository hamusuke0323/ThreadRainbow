package com.hamusuke.threadr.client.gui;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.LeaveRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements ActionListener, WindowListener, ComponentListener {
    private final ThreadRainbowClient client;
    private JMenuBar curMenuBar;
    private Panel curPanel;
    private JPanel south;
    public final JMenuItem packetLog;
    private final JPanel east;

    public MainWindow(ThreadRainbowClient client) {
        super("Thread Rainbow " + Constants.VERSION);
        this.client = client;
        this.addWindowListener(this);
        this.addComponentListener(this);

        this.east = new JPanel(new FlowLayout());
        var logScroll = new JScrollPane(this.client.packetLogTable);
        logScroll.setAutoscrolls(true);
        this.east.add(logScroll);

        this.packetLog = new JMenuItem("ログを見る");
        this.packetLog.addActionListener(this);
        this.packetLog.setActionCommand("packetLog");
    }

    public void tick() {
        this.curPanel.tick();
    }

    public void setPanel(Panel panel) {
        if (this.curPanel != null) {
            this.curPanel.onRemoved();
            this.remove(this.curPanel);
        }

        boolean je = this.curPanel == panel;
        this.curPanel = panel;
        this.curPanel.init();
        this.getContentPane().add(this.curPanel, BorderLayout.CENTER);

        if (!je) {
            this.updateMenuBar();
            this.updateSouth();
        }

        this.revalidate();
        this.repaint();
    }

    public Panel getPanel() {
        return this.curPanel;
    }

    private void updateMenuBar() {
        var bar = this.curPanel.createMenuBar();
        if (bar == null) {
            return;
        }

        this.removeMenuBar();
        this.curMenuBar = bar;
        this.getContentPane().add(this.curMenuBar, BorderLayout.NORTH);
    }

    private void removeMenuBar() {
        if (this.curMenuBar != null) {
            this.remove(this.curMenuBar);
        }
    }

    private void updateSouth() {
        var s = this.curPanel.createSouth();
        if (s == null) {
            return;
        }

        this.removeSouth();
        this.south = s;
        this.south.setPreferredSize(new Dimension(100, this.getHeight() / 4));
        this.getContentPane().add(this.south, BorderLayout.SOUTH);
    }

    private void removeSouth() {
        if (this.south != null) {
            this.remove(this.south);
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

    private void clearChat() {
        if (this.client.chat != null) {
            this.client.chat.clear();
        }
    }

    private void clearPackets() {
        this.client.packetLogTable.clear();
        this.client.packetLogTable.repaint();
    }

    private void exitGame() {
        if (this.client.listener instanceof ClientPlayPacketListener) {
            this.client.getConnection().sendPacket(new ClientCommandReq(Command.EXIT));
        }
    }

    private void leaveRoom() {
        this.reset();
        this.client.setPanel(new CenteredMessagePanel("部屋を出ています..."));
        this.client.getConnection().sendPacket(new LeaveRoomReq());
    }

    public void reset() {
        this.removeMenuBar();
        this.removeSouth();
        this.hidePacketLog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "disconnect":
                this.client.disconnect();
                break;
            case "clearChat":
                this.clearChat();
                break;
            case "packetLog":
                this.showPacketLog();
                break;
            case "hPacketLog":
                this.hidePacketLog();
                break;
            case "clearPackets":
                this.clearPackets();
                break;
            case "exit":
                this.exitGame();
                break;
            case "leave":
                this.leaveRoom();
                break;
            default:
                this.curPanel.actionPerformed(e);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.curPanel.componentResized(e);

        var c = e.getComponent();
        if (c != this) {
            return;
        }

        if (this.south != null) {
            this.south.setPreferredSize(new Dimension(100, c.getHeight() / 4));
            this.south.revalidate();
        }

        this.east.setPreferredSize(new Dimension(c.getWidth() / 3, c.getHeight() / 2));
        this.east.revalidate();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.client.stopLooping();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
