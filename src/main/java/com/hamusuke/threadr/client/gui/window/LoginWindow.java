package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.client.gui.dialog.OkDialog;
import com.hamusuke.threadr.network.protocol.packet.c2s.login.SpiderLoginC2SPacket;
import com.mojang.brigadier.StringReader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.hamusuke.threadr.network.protocol.packet.c2s.login.SpiderLoginC2SPacket.MAX_NAME_LENGTH;

public class LoginWindow extends Window {
    private final String msg;
    private JTextField nameField;

    public LoginWindow(String msg) {
        super("ログイン");
        this.msg = msg;
    }

    @Override
    public void init() {
        super.init();

        if (!this.msg.isEmpty()) {
            new SwingWorker<>() {
                @Override
                protected Object doInBackground() throws Exception {
                    new OkDialog(LoginWindow.this, "エラー", msg);
                    return null;
                }
            }.execute();
        }

        this.menu = this.createMenuBar();
        this.add(this.menu, BorderLayout.NORTH);

        this.nameField = new JTextField("nanashi");
        this.nameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (nameField.getText().length() >= MAX_NAME_LENGTH || (!StringReader.isAllowedInUnquotedString(c) && (c != KeyEvent.VK_BACK_SPACE))) {
                    e.consume();
                }
            }
        });
        var login = new JButton("ログイン");
        var layout = new GridBagLayout();
        var panel = new JPanel(layout);
        addButton(panel, new JLabel("名前を入力してください（英数字のみ）"), layout, 0, 0, 1, 1, 1.0D);
        addButton(panel, this.nameField, layout, 0, 1, 1, 1, 1.0D);
        addButton(panel, login, layout, 0, 2, 1, 1, 1.0D);
        login.setActionCommand("login");
        login.addActionListener(this);
        this.add(panel, BorderLayout.CENTER);
        this.pack();
        this.setSize(this.getWidth() * 2, this.getHeight());
        this.setLocationRelativeTo(null);
    }

    @Nullable
    @Override
    protected JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        var menu = new JMenu("メニュー");
        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        menu.add(disconnect);
        jMenuBar.add(menu);
        return jMenuBar;
    }

    private void login(String name) {
        this.client.getConnection().sendPacket(new SpiderLoginC2SPacket(name.substring(0, Math.min(name.length(), 16))));
    }

    @Override
    protected void onClose() {
        this.client.disconnect();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":
                if (!this.nameField.getText().isEmpty()) {
                    this.dispose();
                    this.login(this.nameField.getText());
                }
                break;
            case "disconnect":
                this.onClose();
                break;
        }
    }
}
