package com.hamusuke.threadr.game.topic;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.client.gui.window.Window;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public record Topic(List<String> lines, String minDescription, String maxDescription) {
    private static final String PREFIXED_LABEL_HTML = "<html><div align=\"center\"><font size=4>%s</font><br/><font size=6>%s</font></div><div align=\"left\" style=\"float: left;\"><font size=2 color=\"#FDE646\">1 %s</font></div><div align=\"right\"><font size=2 color=\"#FDE646\">%s 100</font></div></html>";
    private static final String NORMAL_LABEL_HTML = "<html><div align=\"center\"><font size=6>%s</font></div><div align=\"left\" style=\"float: left;\"><font size=2 color=\"#FDE646\">1 %s</font></div>%s 100</html>";

    public static Topic readFrom(IntelligentByteBuf buf) {
        return new Topic(buf.readList(IntelligentByteBuf::readString, ImmutableList::copyOf), buf.readString(), buf.readString());
    }

    public void writeTo(IntelligentByteBuf buf) {
        buf.writeList(this.lines, (s, buf1) -> buf1.writeString(s));
        buf.writeString(this.minDescription);
        buf.writeString(this.maxDescription);
    }

    public JPanel toPanel() {
        var grid = new GridBagLayout();
        var p = new JPanel(grid);
        var l3 = new JLabel("1 %s %s 100".formatted(this.minDescription, this.maxDescription));
        l3.setHorizontalAlignment(SwingConstants.CENTER);
        l3.setFont(l3.getFont().deriveFont(l3.getFont().getSize2D() * 0.85F));

        if (this.lines.size() == 2) {
            var l = new JLabel(this.lines.get(0));
            l.setHorizontalAlignment(SwingConstants.CENTER);
            var l2 = new JLabel(this.lines.get(1));
            l2.setHorizontalAlignment(SwingConstants.CENTER);
            l2.setFont(l2.getFont().deriveFont(l2.getFont().getSize2D() * 2.0F));

            Window.addButton(p, l, grid, 0, 0, 1, 1, 1.0D);
            Window.addButton(p, l2, grid, 0, 1, 1, 1, 1.0D);
            Window.addButton(p, l3, grid, 0, 2, 1, 1, 1.0D);

            return p;
        }

        var l2 = new JLabel(this.lines.get(0));
        l2.setHorizontalAlignment(SwingConstants.CENTER);
        l2.setFont(l2.getFont().deriveFont(l2.getFont().getSize2D() * 2.0F));

        Window.addButton(p, l2, grid, 0, 0, 1, 1, 1.0D);
        Window.addButton(p, l3, grid, 0, 1, 1, 1, 1.0D);

        return p;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder("lines: [");
        var it = this.lines.iterator();
        while (it.hasNext()) {
            builder.append('"').append(it.next()).append('"');

            if (it.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.append("], minDescription: \"").append(this.minDescription).append('"').append(", maxDescription: \"").append(this.maxDescription).append('"').toString();
    }
}
