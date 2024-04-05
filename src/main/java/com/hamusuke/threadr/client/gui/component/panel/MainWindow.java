package com.hamusuke.threadr.client.gui.component.panel;

import com.hamusuke.threadr.client.gui.window.Window;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

public class MainWindow extends Window {
    private JPanel east;

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

    private void exitGame() {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.EXIT));
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
    }

    private synchronized void showPacketLog() {
        this.add(this.east, BorderLayout.EAST);
        //this.packetLog.setText("ログを閉じる");
        //this.packetLog.setActionCommand("hPacketLog");
        this.revalidate();
    }

    private synchronized void hidePacketLog() {
        this.remove(this.east);
        //this.packetLog.setText("ログを見る");
        //this.packetLog.setActionCommand("packetLog");
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
