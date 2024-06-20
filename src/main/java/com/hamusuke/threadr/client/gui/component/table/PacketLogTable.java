package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.network.PacketLogger.PacketDetails;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class PacketLogTable extends JTable {
    private static final int MAX_LOG_SIZE = 256;
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"送信", "受信"}, 0);

    public PacketLogTable() {
        super(MODEL);
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setCellSelectionEnabled(false);
        this.setRowHeight(30);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return new PacketInfoRenderer();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void addSent(PacketDetails details) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.addSent(details));
        } else {
            MODEL.addRow(new Object[]{details, null});
            this.clearOverflowed();
            var client = ThreadRainbowClient.getInstance();
            client.getMainWindow().onPacketLog();
        }
    }

    public void addReceived(PacketDetails details) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.addReceived(details));
        } else {
            MODEL.addRow(new Object[]{null, details});
            this.clearOverflowed();
            var client = ThreadRainbowClient.getInstance();
            client.getMainWindow().onPacketLog();
        }
    }

    private void clearOverflowed() {
        var vec = MODEL.getDataVector();
        if (vec.size() <= MAX_LOG_SIZE) {
            return;
        }

        vec.remove(0);
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
        } else {
            MODEL.getDataVector().clear();
            MODEL.setRowCount(0);
        }
    }
}
