package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.client.gui.component.panel.pre.ServerListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class ConnectingPanel extends Panel {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String host;
    private final int port;

    public ConnectingPanel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void init() {
        super.init();

        var label = new JLabel(String.format("%s:%d に接続しています...", this.host, this.port), SwingConstants.CENTER);
        this.add(new JLabel(String.format("%s:%d に接続中", this.host, this.port), SwingConstants.CENTER), BorderLayout.NORTH);
        this.add(label, BorderLayout.CENTER);

        new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                client.sendMsg(() -> {
                    try {
                        client.connectToServer(host, port, label::setText);
                    } catch (Exception e) {
                        LOGGER.warn("Connection Error!", e);
                        client.setPanel(new OkPanel(new ServerListPanel(), "エラー", String.format("%s:%d に接続できませんでした\n%s", host, port, e)));
                    }
                });

                return null;
            }
        }.execute();
    }
}
