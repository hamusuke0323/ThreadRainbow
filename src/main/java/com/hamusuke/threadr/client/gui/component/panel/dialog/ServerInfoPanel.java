package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
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

public class ServerInfoPanel extends Panel {
    private final Panel parent;
    private final Consumer<ServerInfoPanel> callback;
    @Nullable
    private final ServerInfo info;
    private String address;
    private int port;
    private boolean accepted;

    public ServerInfoPanel(Panel parent, Consumer<ServerInfoPanel> callback) {
        this(parent, callback, null);
    }

    public ServerInfoPanel(Panel parent, Consumer<ServerInfoPanel> callback, @Nullable ServerInfo info) {
        super(new GridBagLayout());
        this.parent = parent;
        this.callback = callback;
        this.info = info;
    }

    @Override
    public void init() {
        super.init();

        var host = new JTextField();
        host.setName("host");
        if (this.info == null) {
            try {
                host.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ignored) {
                host.setText("localhost");
            }
        } else {
            host.setText(this.info.address);
        }
        host.setToolTipText("host ip/name");

        var port = new JTextField();
        port.setName("port");
        port.setText(this.info == null ? "16160" : this.info.port + "");
        port.setToolTipText("port");
        port.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c < '0' || c > '9') && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        var l = (GridBagLayout) getLayout();
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
            this.onClose();
        });
        var no = new JButton("キャンセル");
        no.addActionListener(e -> {
            this.accepted = false;
            this.onClose();
        });
        addButton(this, host, l, 0, 0, 1, 1, 0.125D);
        addButton(this, port, l, 0, 1, 1, 1, 0.125D);
        addButton(this, yes, l, 0, 2, 1, 1, 0.125D);
        addButton(this, no, l, 0, 3, 1, 1, 0.125D);
    }

    @Override
    public void onClose() {
        if (this.accepted) {
            this.callback.accept(this);
        }

        this.client.setPanel(this.parent);
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }
}
