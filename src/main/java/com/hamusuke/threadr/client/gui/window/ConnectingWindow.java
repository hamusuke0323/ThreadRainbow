package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.client.gui.dialog.DedicatedConnectingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectingWindow extends Window {
    public ConnectingWindow() {
        super("サーバーに接続");
    }

    @Override
    public void init() {
        super.init();

        var host = new JTextField();
        host.setName("host");
        try {
            host.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ignored) {
            host.setText("localhost");
        }
        host.setToolTipText("host ip/name");

        var port = new JTextField();
        port.setName("port");
        port.setText("8080");
        port.setToolTipText("port");
        port.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c < '0' || c > '9') && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        var button = new JButton("接続");
        button.addActionListener(e -> {
            new DedicatedConnectingDialog(this, this.client, host.getText(), Integer.parseInt(port.getText()));
        });

        var layout = new GridBagLayout();
        var panel = new JPanel(layout);
        addButton(panel, host, layout, 0, 0, 1, 1, 1.0D);
        addButton(panel, port, layout, 0, 1, 1, 1, 1.0D);
        addButton(panel, button, layout, 0, 2, 1, 1, 1.0D);

        this.add(panel, BorderLayout.CENTER);
        this.setSize(350, 120);
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onClose() {
        this.client.stopLooping();
        super.onClose();
    }
}
