package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.network.protocol.packet.Packet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class PacketLogTable extends JTable {
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

    public void addSent(Packet<?> packet) {
        MODEL.addRow(new Object[]{packet, null});
    }

    public void addReceived(Packet<?> packet) {
        MODEL.addRow(new Object[]{null, packet});
    }

    public void clear() {
        MODEL.getDataVector().clear();
        MODEL.setRowCount(0);
    }
}
