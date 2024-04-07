package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class EnterPasswordPanel extends Panel {
    private final Consumer<EnterPasswordPanel> callback;
    private boolean accepted;
    private JTextField password;

    public EnterPasswordPanel(Consumer<EnterPasswordPanel> callback) {
        super(new GridBagLayout());
        this.callback = callback;
    }

    @Override
    public void init() {
        super.init();

        var title = new JLabel("参加するにはパスワードを入力してください", SwingConstants.CENTER);
        this.password = new JTextField("password");
        this.password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (Character.isSpaceChar(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        var ok = new JButton("OK");
        ok.addActionListener(e -> this.enter());
        var cancel = new JButton("キャンセル");
        cancel.addActionListener(e -> this.onClose());

        var l = (GridBagLayout) getLayout();
        addButton(this, title, l, 0, 0, 1, 1, 0.125D);
        addButton(this, this.password, l, 0, 1, 1, 1, 0.125D);
        addButton(this, ok, l, 0, 2, 1, 1, 0.125D);
        addButton(this, cancel, l, 0, 3, 1, 1, 0.125D);
    }

    private void enter() {
        var pwd = this.getPassword();
        if (pwd.isEmpty() || pwd.isBlank()) {
            return;
        }

        this.accepted = true;
        this.onClose();
    }

    public String getPassword() {
        var password = this.password.getText();
        return Util.filterBy(password.substring(0, Math.min(password.length(), Room.MAX_ROOM_PASSWD_LENGTH)), value -> !Character.isSpaceChar(value));
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public void onClose() {
        this.callback.accept(this);
    }
}
