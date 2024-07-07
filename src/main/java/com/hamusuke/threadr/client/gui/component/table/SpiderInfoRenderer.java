package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.client.network.spider.AbstractClientSpider;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SpiderInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof AbstractClientSpider spider) {
            var label = new JLabel(spider.getName(), CENTER);
            label.setForeground(spider.myTeam == null ? Color.BLACK : spider.myTeam.getColor());
            label.setToolTipText(String.format("id: %s, ping: %dms", spider.getId(), spider.getPing()));
            return label;
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
