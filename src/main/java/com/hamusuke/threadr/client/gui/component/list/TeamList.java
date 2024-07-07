package com.hamusuke.threadr.client.gui.component.list;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.game.team.TeamEntry;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.TeamToggleReq;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TeamList extends JList<TeamEntry> {
    private static final DefaultListModel<TeamEntry> MODEL = new DefaultListModel<>();
    private final ThreadRainbowClient client;

    public TeamList(ThreadRainbowClient client) {
        super(MODEL);

        this.client = client;
        this.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var p = new JPanel();
            var l = new JLabel(value.getSpider().getName());
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setForeground(value.getType().getColor());
            p.add(l);
            return p;
        });
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int i = locationToIndex(e.getPoint());
                if (i != -1) {
                    onSelected(i);
                }
            }
        });
    }

    public void addTeamEntries(List<TeamEntry> entries) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.addTeamEntries(entries));
            return;
        }

        MODEL.addAll(entries);
    }

    public void onMakingTeamDone(List<TeamEntry> entries) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.onMakingTeamDone(entries));
            return;
        }

        this.clear();
        this.addTeamEntries(entries);
    }

    public List<TeamEntry> getTeamEntries() {
        return ImmutableList.copyOf(MODEL.elements().asIterator());
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
            return;
        }

        MODEL.clear();
    }

    private void onSelected(int index) {
        if (!this.client.amIHost()) {
            return;
        }

        var e = MODEL.get(index);
        if (e != null) {
            e.toggleTeam();
            this.client.getConnection().sendPacket(new TeamToggleReq(e.toSerializer()));
        }
    }
}
