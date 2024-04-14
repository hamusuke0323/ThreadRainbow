package com.hamusuke.threadr.client.network;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.ChatReq;

import javax.swing.*;

public class Chat {
    private final JTextArea textArea;
    private final JScrollPane scrollTextArea;
    private final JTextField field;
    private final JScrollPane scrollField;

    public Chat(ThreadRainbowClient client) {
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.scrollTextArea = new JScrollPane(this.textArea);

        this.field = new JTextField();
        this.field.addActionListener(e -> {
            var s = this.field.getText();
            if (!s.isEmpty()) {
                client.getConnection().sendPacket(new ChatReq(s));
                this.field.setText("");
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

    public void clear() {
        this.textArea.setText("");
        this.scrollToMax();
    }
}
