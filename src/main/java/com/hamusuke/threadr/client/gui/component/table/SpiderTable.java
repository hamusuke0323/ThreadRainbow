package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SpiderTable extends JTable {
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"spiders (ping[ms])"}, 0);
    protected final ThreadRainbowClient client;

    public SpiderTable(ThreadRainbowClient client) {
        super(MODEL);
        this.client = client;
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setCellSelectionEnabled(false);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        this.clear();
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.forEach(spider -> {
                if (MODEL.getColumnCount() == 2) {
                    var num = "???";
                    if (spider instanceof LocalSpider local) {
                        num = Byte.toString(local.getLocalCard().getNumber());
                    } else if (spider instanceof RemoteSpider remote && remote.getRemoteCard() != null && remote.getRemoteCard().canBeSeen()) {
                        num = Byte.toString(remote.getRemoteCard().getNumber());
                    }

                    MODEL.addRow(new Object[]{"%s (%sms)".formatted(spider.getName(), spider.getPing()), num});
                } else {
                    MODEL.addRow(new Object[]{"%s (%sms)".formatted(spider.getName(), spider.getPing())});
                }
            });
        }
    }

    public void addCardNumCol() {
        if (this.getColumnCount() == 2) {
            return;
        }

        MODEL.addColumn("cards");
    }

    public void removeCardNumCol() {
        MODEL.setColumnCount(1);
    }

    public void clear() {
        MODEL.setRowCount(0);
    }
}
