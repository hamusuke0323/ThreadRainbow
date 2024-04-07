package com.hamusuke.threadr.client.gui.component.panel.dialog;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class NewRoomPanel extends Panel {
    private final Consumer<NewRoomPanel> callback;
    private boolean accepted;
    private JCheckBox makePassword;
    private JTextField name;
    private JTextField password;

    public NewRoomPanel(Consumer<NewRoomPanel> callback) {
        super(new GridBagLayout());
        this.callback = callback;
    }

    @Override
    public void init() {
        super.init();

        var nameTitle = new JLabel("部屋の名前（%s文字まで）".formatted(Room.MAX_ROOM_NAME_LENGTH), SwingConstants.CENTER);
        this.name = new JTextField(String.format("%sの部屋", this.client.clientSpider.getName()));
        this.makePassword = new JCheckBox("パスワードあり");
        this.makePassword.addActionListener(e -> this.password.setEnabled(this.makePassword.isSelected()));
        this.password = new JTextField("password");
        this.password.setEnabled(this.makePassword.isSelected());
        this.password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (Character.isSpaceChar(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        var create = new JButton("作成して参加する");
        create.addActionListener(e -> this.create());
        var cancel = new JButton("キャンセル");
        cancel.addActionListener(e -> this.onClose());

        var l = (GridBagLayout) getLayout();
        addButton(this, nameTitle, l, 0, 0, 1, 1, 0.125D);
        addButton(this, this.name, l, 0, 1, 1, 1, 0.125D);
        addButton(this, this.makePassword, l, 0, 2, 1, 1, 0.125D);
        addButton(this, this.password, l, 0, 3, 1, 1, 0.125D);
        addButton(this, create, l, 0, 4, 1, 1, 0.125D);
        addButton(this, cancel, l, 0, 5, 1, 1, 0.125D);
    }

    private void create() {
        var name = this.getRoomName();
        if (name.isEmpty() || name.isBlank()) {
            return;
        }

        var pwd = this.getPassword();
        if (this.hasPassword() && (pwd.isEmpty() || pwd.isBlank())) {
            return;
        }

        this.accepted = true;
        this.onClose();
    }

    public String getRoomName() {
        var name = this.name.getText();
        return name.substring(0, Math.min(name.length(), Room.MAX_ROOM_NAME_LENGTH));
    }

    public String getPassword() {
        var password = this.password.getText();
        return Util.filterBy(password.substring(0, Math.min(password.length(), Room.MAX_ROOM_PASSWD_LENGTH)), value -> !Character.isSpaceChar(value));
    }

    public boolean hasPassword() {
        return this.makePassword.isSelected();
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public void onClose() {
        this.callback.accept(this);
    }
}
