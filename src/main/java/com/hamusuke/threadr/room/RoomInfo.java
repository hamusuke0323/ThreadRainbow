package com.hamusuke.threadr.room;

import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public record RoomInfo(int id, String roomName, String hostName, int population, boolean hasPassword) {
    public RoomInfo(IntelligentByteBuf buf) {
        this(buf.readVariableInt(), buf.readString(), buf.readString(), buf.readInt(), buf.readBoolean());
    }

    public void writeTo(IntelligentByteBuf buf) {
        buf.writeVariableInt(this.id);
        buf.writeString(this.roomName);
        buf.writeString(this.hostName);
        buf.writeInt(this.population);
        buf.writeBoolean(this.hasPassword);
    }

    public JPanel toPanel() {
        var label = new JLabel(Util.toHTML(String.format("%s\nホスト: %s\n%d人\n%s", this.roomName, this.hostName, this.population, this.hasPassword ? "パスワードあり" : "パスワードなし")), SwingConstants.CENTER);
        var panel = new JPanel();
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(label);
        return panel;
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", population=" + population +
                ", hasPassword=" + hasPassword +
                '}';
    }
}
