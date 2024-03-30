package com.hamusuke.threadr.client.gui.dialog;

import com.hamusuke.threadr.client.gui.window.Window;
import com.hamusuke.threadr.network.ServerInfo;
import com.hamusuke.threadr.util.Util;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class ServerInfoDialog extends JDialog {
    private final Consumer<ServerInfoDialog> callback;
    private String address;
    private int port;
    private boolean accepted;

    public ServerInfoDialog(Window owner, Consumer<ServerInfoDialog> callback) {
        this(owner, callback, null);
    }

    public ServerInfoDialog(Window owner, Consumer<ServerInfoDialog> callback, @Nullable ServerInfo info) {
        super(owner, "サーバーを追加", true);

        this.callback = callback;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        var host = new JTextField();
        host.setName("host");
        if (info == null) {
            try {
                host.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ignored) {
                host.setText("localhost");
            }
        } else {
            host.setText(info.address);
        }

        host.setToolTipText("host ip/name");

        var port = new JTextField();
        port.setName("port");
        port.setText(info == null ? "16160" : info.port + "");
        port.setToolTipText("port");
        port.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c < '0' || c > '9') && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        this.add(host, BorderLayout.NORTH);
        this.add(port, BorderLayout.CENTER);

        var l = new GridBagLayout();
        var south = new JPanel(l);
        var yes = new JButton("OK");
        yes.addActionListener(e -> {
            if (host.getText().isEmpty() || Util.numberOnly(port.getText()).isEmpty()) {
                return;
            }

            this.address = host.getText();
            try {
                this.port = Integer.parseInt(Util.numberOnly(port.getText()));
            } catch (NumberFormatException ignored) {
                return;
            }

            this.accepted = true;
            this.dispose();
        });
        var no = new JButton("キャンセル");
        no.addActionListener(e -> {
            this.accepted = false;
            this.dispose();
        });
        Window.addButton(south, yes, l, 0, 0, 1, 1, 0.125D);
        Window.addButton(south, no, l, 0, 1, 1, 1, 0.125D);
        this.add(south, BorderLayout.SOUTH);
        this.pack();
        this.setSize(new Dimension(this.getWidth() * 2, this.getHeight()));
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (this.accepted) {
            this.callback.accept(this);
        }
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }
}
