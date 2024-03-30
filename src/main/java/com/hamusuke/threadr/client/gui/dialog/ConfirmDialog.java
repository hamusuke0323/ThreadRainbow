package com.hamusuke.threadr.client.gui.dialog;

import com.hamusuke.threadr.client.gui.window.Window;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ConfirmDialog extends JDialog {
    private final Consumer<Boolean> yesNo;
    private boolean accepted;

    public ConfirmDialog(Window owner, String text, Consumer<Boolean> yesNo) {
        super(owner, "確認", true);

        this.yesNo = yesNo;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(new JLabel(text), BorderLayout.NORTH);
        var yes = new JButton("はい");
        yes.addActionListener(e -> {
            this.accepted = true;
            this.dispose();
        });
        var no = new JButton("キャンセル");
        no.addActionListener(e -> {
            this.accepted = false;
            this.dispose();
        });
        this.add(yes, BorderLayout.CENTER);
        this.add(no, BorderLayout.SOUTH);
        this.pack();
        this.setSize(new Dimension(this.getWidth() * 2, this.getHeight()));
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();

        this.yesNo.accept(this.accepted);
    }
}
