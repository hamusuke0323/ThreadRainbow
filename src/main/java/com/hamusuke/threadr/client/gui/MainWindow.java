package com.hamusuke.threadr.client.gui;

import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements ActionListener, KeyListener, WindowListener, ComponentListener {
    private final ThreadRainbowClient client;
    private JMenuBar curMenuBar;
    private Panel curPanel;
    private JPanel south;

    public MainWindow(ThreadRainbowClient client) {
        super("Thread Rainbow " + Constants.VERSION);
        this.client = client;
        this.addWindowListener(this);
        this.addComponentListener(this);
        this.addKeyListener(this);
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

        if (this.curMenuBar != null) {
            this.remove(this.curMenuBar);
        }

        this.curMenuBar = bar;
        this.getContentPane().add(this.curMenuBar, BorderLayout.NORTH);
    }

    private void updateSouth() {
        var s = this.curPanel.createSouth();
        if (s == null) {
            return;
        }

        if (this.south != null) {
            this.remove(this.south);
        }

        this.south = s;
        this.getContentPane().add(this.south, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "disconnect":
                this.client.disconnect();
                break;
            case "exit":
                this.client.getConnection().sendPacket(new ClientCommandReq(Command.EXIT));
                break;
            default:
                this.curPanel.actionPerformed(e);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.curPanel.componentResized(e);

        var c = e.getComponent();
        if (c == this && this.south != null) {
            this.south.setPreferredSize(new Dimension(100, c.getHeight() / 4));
            this.south.revalidate();
        }
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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.curPanel.onClose();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
