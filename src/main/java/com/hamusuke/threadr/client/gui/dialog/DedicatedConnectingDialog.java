package com.hamusuke.threadr.client.gui.dialog;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.ConnectingWindow;
import com.hamusuke.threadr.client.gui.window.Window;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;

public class DedicatedConnectingDialog extends JDialog {
    public DedicatedConnectingDialog(Window owner, ThreadRainbowClient client, String host, int port) {
        super(owner, String.format("Connecting to %s:%d...", host, port), true);
        JLabel label = new JLabel(String.format("Connecting to %s:%d...", host, port));
        this.add(label);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setSize(this.getWidth() * 2, this.getHeight() * 2);
        this.setLocationRelativeTo(null);
        new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                try {
                    client.sendMsg(() -> client.connectToServer(host, port, label::setText, () -> new SwingWorker<>() {
                        @Override
                        protected Object doInBackground() {
                            DedicatedConnectingDialog.this.getOwner().dispose();
                            return null;
                        }
                    }.execute()));
                } catch (Exception e) {
                    LogManager.getLogger().warn("Error!", e);
                    DedicatedConnectingDialog.this.getOwner().dispose();
                    client.setCurrentWindow(new ConnectingWindow());
                }
                return null;
            }
        }.execute();
        this.setVisible(true);
    }
}
