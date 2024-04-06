package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ConfirmPanel extends Panel {
    private final Panel parent;
    private final String text;
    private final Consumer<Boolean> yesNo;
    private boolean accepted;

    public ConfirmPanel(Panel panel, String text, Consumer<Boolean> yesNo) {
        super(new GridBagLayout());
        this.parent = panel;
        this.text = text;
        this.yesNo = yesNo;
    }

    @Override
    public void init() {
        super.init();

        var title = new JLabel("確認", SwingConstants.CENTER);
        var label = new JLabel(this.text, SwingConstants.CENTER);
        var yes = new JButton("はい");
        yes.addActionListener(e -> {
            this.accepted = true;
            this.onClose();
        });
        var no = new JButton("キャンセル");
        no.addActionListener(e -> {
            this.accepted = false;
            this.onClose();
        });
        var l = (GridBagLayout) getLayout();
        addButton(this, title, l, 0, 0, 1, 1, 0.125D);
        addButton(this, label, l, 0, 1, 1, 1, 0.125D);
        addButton(this, yes, l, 0, 2, 1, 1, 0.125D);
        addButton(this, no, l, 0, 3, 1, 1, 0.125D);
    }

    @Override
    public void onClose() {
        this.yesNo.accept(this.accepted);
        this.client.setPanel(this.parent);
    }
}
