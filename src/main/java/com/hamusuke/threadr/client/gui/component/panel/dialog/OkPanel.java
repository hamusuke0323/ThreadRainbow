package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import java.awt.*;

public class OkPanel extends com.hamusuke.threadr.client.gui.component.panel.Panel {
    private final com.hamusuke.threadr.client.gui.component.panel.Panel parent;
    private final String title;
    private final String message;

    public OkPanel(Panel parent, String title, String nonHTMLText) {
        this.parent = parent;
        this.title = title;
        this.message = Util.toHTML(nonHTMLText);
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle(this.title);
        var label = new JLabel(this.message);
        this.add(label, BorderLayout.CENTER);
        var ok = new JButton("OK");
        ok.addActionListener(e -> this.onClose());
        this.add(ok, BorderLayout.SOUTH);
    }

    @Override
    public void onClose() {
        this.client.setPanel(this.parent);
    }
}
