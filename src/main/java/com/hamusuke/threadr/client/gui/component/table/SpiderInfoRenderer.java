package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.network.Spider;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class SpiderInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        AtomicReference<JLabel> label = new AtomicReference<>();
        label.set((JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));

        searchByClass(table, row, Spider.class, (spider, integer) -> {
            label.set((JLabel) super.getTableCellRendererComponent(table, spider, isSelected, hasFocus, row, integer));
            label.get().setText(spider.getName());
            label.get().setToolTipText(spider.getName());
        });

        return label.get();
    }

    public static <T> void searchByClass(JTable table, int row, Class<T> target, BiConsumer<T, Integer> biConsumer) {
        var model = table.getModel();
        if (model.getColumnCount() == 0 || model.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < model.getColumnCount(); i++) {
            var o = model.getValueAt(row, i);
            if (target.isInstance(o)) {
                biConsumer.accept(target.cast(o), i);
                break;
            }
        }
    }
}
