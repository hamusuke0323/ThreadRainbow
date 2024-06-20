package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class SpiderTable extends JTable {
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"クモ"}, 0);
    protected final ThreadRainbowClient client;

    public SpiderTable(ThreadRainbowClient client) {
        super(MODEL);
        this.client = client;
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setCellSelectionEnabled(false);
        this.setRowHeight(30);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return this.getColumnName(column).equals("クモ") ? new SpiderInfoRenderer() : super.getCellRenderer(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        this.clear();
        if (this.client.curRoom == null) {
            return;
        }

        synchronized (this.client.curRoom.getSpiders()) {
            this.client.curRoom.getSpiders().forEach(spider -> {
                if (MODEL.getColumnCount() == 2) {
                    var num = "???";
                    if (spider instanceof LocalSpider local) {
                        num = Byte.toString(local.getLocalCard().getNumber());
                    } else if (spider instanceof RemoteSpider remote && remote.getRemoteCard() != null && remote.getRemoteCard().canBeSeen()) {
                        num = Byte.toString(remote.getRemoteCard().getNumber());
                    }

                    MODEL.addRow(new Object[]{spider, num});
                } else {
                    MODEL.addRow(new Object[]{spider});
                }
            });
        }
    }

    public void addCardNumCol() {
        if (this.getColumnCount() == 2) {
            return;
        }

        MODEL.addColumn("カード");
    }

    public void removeCardNumCol() {
        MODEL.setColumnCount(1);
    }

    public void clear() {
        MODEL.getDataVector().clear();
        MODEL.setRowCount(0);
    }
}
