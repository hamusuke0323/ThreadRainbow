package com.hamusuke.threadr.client.gui.component.list;

import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class JTopicList extends JList<TopicEntry> {
    private static final DefaultListModel<TopicEntry> MODEL = new DefaultListModel<>();

    public JTopicList() {
        super(MODEL);

        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setOpaque(true);
        this.setDragEnabled(false);
        this.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel(Util.toHTML(value.toPrettyString()));
            label.setForeground(isSelected ? this.getSelectionForeground() : Color.BLACK);
            var panel = new JPanel();
            panel.setBackground(isSelected ? this.getSelectionBackground() : Color.WHITE);
            panel.setBorder(new LineBorder(isSelected ? this.getSelectionForeground() : this.getSelectionBackground(), 1));
            panel.add(label);
            return panel;
        });
    }

    public void addTopics(List<TopicEntry> entries) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.addTopics(entries));
            return;
        }

        MODEL.addAll(entries);
    }

    public void removeTopic(TopicEntry entry) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.removeTopic(entry));
            return;
        }

        MODEL.removeElement(entry);
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
            return;
        }

        MODEL.clear();
    }
}
