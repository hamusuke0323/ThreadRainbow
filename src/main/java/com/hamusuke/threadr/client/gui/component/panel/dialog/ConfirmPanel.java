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
        this.parent = panel;
        this.text = text;
        this.yesNo = yesNo;
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("確認");
        this.add(new JLabel(this.text), BorderLayout.NORTH);
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
        this.add(yes, BorderLayout.CENTER);
        this.add(no, BorderLayout.SOUTH);
    }

    @Override
    public void onClose() {
        this.yesNo.accept(this.accepted);
        this.client.setPanel(this.parent);
    }
}
