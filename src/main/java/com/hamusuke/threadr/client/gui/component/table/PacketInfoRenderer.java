package com.hamusuke.threadr.client.gui.component.table;

import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.util.PacketUtil;
import com.hamusuke.threadr.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PacketInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (!(value instanceof Packet<?> packet)) {
            if (column == 0) {
                var l = new JLabel("←");
                l.setHorizontalAlignment(RIGHT);
                return l;
            }

            return new JLabel("→");
        }

        var l = new JLabel(packet.getClass().getSimpleName());
        var details = PacketUtil.getPacketDetails(packet);
        l.setToolTipText(Util.toHTML(details));

        return l;
    }
}
