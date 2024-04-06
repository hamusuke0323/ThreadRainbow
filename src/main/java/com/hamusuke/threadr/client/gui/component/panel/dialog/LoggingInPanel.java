package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;

import javax.swing.*;
import java.awt.*;

public class LoggingInPanel extends Panel {
    @Override
    public void init() {
        super.init();

        var label = new JLabel("ログインしています...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(label, BorderLayout.CENTER);
    }
}
