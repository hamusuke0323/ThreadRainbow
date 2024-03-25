package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.client.ThreadRainbowClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SpiderTable extends JTable {
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"spiders", "ping"}, 0);
    protected final ThreadRainbowClient client;

    public SpiderTable(ThreadRainbowClient client) {
        super(MODEL);
        this.client = client;
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setCellSelectionEnabled(false);
        //this.getColumnModel().getColumn(0).setCellRenderer(new SpiderInfoRenderer());
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        this.clear();
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.stream().forEach(spider -> {
                MODEL.addRow(new Object[]{spider.getName(), spider.getPing() + "ms"});
            });
        }
    }

    public void clear() {
        MODEL.setRowCount(0);
    }
}
