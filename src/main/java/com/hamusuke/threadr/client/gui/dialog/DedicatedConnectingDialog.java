package com.hamusuke.threadr.client.gui.dialog;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.ServerListWindow;
import com.hamusuke.threadr.client.gui.window.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class DedicatedConnectingDialog extends JDialog {
    private static final Logger LOGGER = LogManager.getLogger();

    public DedicatedConnectingDialog(Window owner, ThreadRainbowClient client, String host, int port) {
        super(owner, String.format("%s:%dに接続しています...", host, port), true);
        JLabel label = new JLabel(String.format("%s:%d に接続しています...", host, port));
        this.add(label);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setSize(this.getWidth() * 2, this.getHeight() * 2);
        this.setLocationRelativeTo(owner);
        new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                client.sendMsg(() -> {
                    try {
                        client.connectToServer(host, port, label::setText, () -> new SwingWorker<>() {
                            @Override
                            protected Object doInBackground() {
                                DedicatedConnectingDialog.this.getOwner().dispose();
                                return null;
                            }
                        }.execute());
                    } catch (Exception e) {
                        LOGGER.warn("Connection Error!", e);
                        DedicatedConnectingDialog.this.getOwner().dispose();
                        client.setCurrentWindow(new ServerListWindow(String.format("%s:%d に接続できませんでした\n%s", host, port, e)));
                    }
                });

                return null;
            }
        }.execute();
        this.setVisible(true);
    }
}
