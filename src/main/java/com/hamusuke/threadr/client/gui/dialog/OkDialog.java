package com.hamusuke.threadr.client.gui.dialog;

import com.hamusuke.threadr.client.gui.window.Window;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import java.awt.*;

public class OkDialog extends JDialog {
    public OkDialog(Window owner, String title, String nonHTMLText) {
        super(owner, title, true);

        this.setLocationRelativeTo(owner);
        var label = new JLabel(Util.toHTML(nonHTMLText));
        this.add(label, BorderLayout.CENTER);
        var ok = new JButton("OK");
        ok.addActionListener(e -> this.dispose());
        this.add(ok, BorderLayout.SOUTH);
        this.pack();
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }
}
