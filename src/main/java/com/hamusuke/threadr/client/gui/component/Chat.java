package com.hamusuke.threadr.client.gui.component;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.protocol.packet.c2s.common.ChatC2SPacket;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Chat {
    private final JTextArea textArea;
    private final JScrollPane scrollTextArea;
    private final JTextField field;
    private final JScrollPane scrollField;

    public Chat(ThreadRainbowClient client) {
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.textArea.setAutoscrolls(true);
        this.scrollTextArea = new JScrollPane(this.textArea);
        this.scrollTextArea.setAutoscrolls(true);

        this.field = new JTextField();
        this.field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    client.getConnection().sendPacket(new ChatC2SPacket(Chat.this.field.getText()));
                    Chat.this.field.setText("");
                    e.consume();
                }
            }
        });
        this.scrollField = new JScrollPane(this.field);
        this.scrollField.setAutoscrolls(true);
    }

    public void addMessage(String msg) {
        this.textArea.append(msg + "\n");
        this.scrollToMax();
    }

    public void scrollToMax() {
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    }

    public JScrollPane getTextArea() {
        return this.scrollTextArea;
    }

    public JScrollPane getField() {
        return this.scrollField;
    }
}
