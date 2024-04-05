package com.hamusuke.threadr.client.gui.component.panel;

import com.hamusuke.threadr.network.protocol.packet.serverbound.login.EnterNameRsp;
import com.mojang.brigadier.StringReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.hamusuke.threadr.network.protocol.packet.serverbound.login.EnterNameRsp.MAX_NAME_LENGTH;

public class LoginPanel extends Panel {
    private JTextField nameField;

    @Override
    public void init() {
        super.init();

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
        this.setSize(this.getWidth() * 2, this.getHeight());
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        var menu = new JMenu("メニュー");
        var disconnect = new JMenuItem("切断");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this.client.getMainWindow());
        menu.add(disconnect);
        jMenuBar.add(menu);
        return jMenuBar;
    }

    private void login(String name) {
        this.client.getConnection().sendPacket(new EnterNameRsp(name.substring(0, Math.min(name.length(), 16))));
    }

    @Override
    public void onClose() {
        this.client.disconnect();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!this.nameField.getText().isEmpty()) {
            this.login(this.nameField.getText());
        }
    }
}
