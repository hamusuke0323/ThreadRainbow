package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import java.awt.*;

public class OkPanel extends Panel {
    private final Panel parent;
    private final String title;
    private final String message;

    public OkPanel(Panel parent, String title, String nonHTMLText) {
        super(new GridBagLayout());
        this.parent = parent;
        this.title = title;
        this.message = Util.toHTML(nonHTMLText);
    }

    @Override
    public void init() {
        super.init();

        var title = new JLabel(this.title, SwingConstants.CENTER);
        var label = new JLabel(this.message, SwingConstants.CENTER);
        var ok = new JButton("OK");
        ok.addActionListener(e -> this.onClose());
        var l = (GridBagLayout) getLayout();
        addButton(this, title, l, 0, 0, 1, 1, 0.125D);
        addButton(this, label, l, 0, 1, 1, 1, 0.125D);
        addButton(this, ok, l, 0, 2, 1, 1, 0.125D);
    }

    @Override
    public void onClose() {
        this.client.setPanel(this.parent);
    }
}
