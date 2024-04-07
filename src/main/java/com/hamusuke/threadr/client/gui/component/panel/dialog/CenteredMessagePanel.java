package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;

import javax.swing.*;
import java.awt.*;

public class CenteredMessagePanel extends Panel {
    protected final String msg;

    public CenteredMessagePanel(String msg) {
        this.msg = msg;
    }

    @Override
    public void init() {
        super.init();
        this.add(new JLabel(this.msg, SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
