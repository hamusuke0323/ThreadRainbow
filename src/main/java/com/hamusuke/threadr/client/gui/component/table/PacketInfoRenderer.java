package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.network.PacketLogger.PacketDetails;
import com.hamusuke.threadr.util.PacketUtil;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PacketInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (!(value instanceof PacketDetails details)) {
            if (column == 0) {
                var l = new JLabel("←");
                l.setHorizontalAlignment(RIGHT);
                return l;
            }

            return new JLabel("→");
        }

        var byteStr = "(%s)".formatted(PacketUtil.convertBytes(details.size()));
        var l = new JLabel(details.packet().getClass().getSimpleName() + byteStr);
        var packetDetails = PacketUtil.getPacketDetails(details.packet(), byteStr);
        l.setToolTipText(Util.toHTML(packetDetails));

        return l;
    }
}
