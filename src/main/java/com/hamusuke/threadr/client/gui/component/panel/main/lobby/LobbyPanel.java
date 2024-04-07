package com.hamusuke.threadr.client.gui.component.panel.main.lobby;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.NewRoomPanel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.CreateRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.JoinRoomReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListQueryReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.RoomListReq;
import com.hamusuke.threadr.room.Room;
import com.hamusuke.threadr.room.RoomInfo;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;

public class LobbyPanel extends Panel implements ListSelectionListener {
    private JList<RoomInfo> list;
    private JTextField searchBox;
    private JButton search;
    private JButton join;
    private JButton refresh;
    private int refreshTicks;

    @Override
    public void init() {
        super.init();

        var sl = new GridBagLayout();
        var searchPanel = new JPanel(sl);
        this.searchBox = new JTextField();
        this.searchBox.setToolTipText("検索");
        this.search = new JButton("検索");
        this.search.setActionCommand("search");
        this.search.addActionListener(this);
        addButton(searchPanel, this.searchBox, sl, 0, 0, 1, 1, 0.125D);
        addButton(searchPanel, this.search, sl, 1, 0, 1, 1, 0.2D, 0.125D);
        this.add(searchPanel, BorderLayout.NORTH);

        var model = new DefaultListModel<RoomInfo>();
        this.list = new JList<>(model);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.addListSelectionListener(this);
        this.list.setOpaque(true);
        this.list.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var p = value.toPanel();
            p.setBackground(isSelected ? this.list.getSelectionBackground() : Color.WHITE);
            return p;
        });
        this.add(new JScrollPane(this.list), BorderLayout.CENTER);

        var l = new GridBagLayout();
        var south = new JPanel(l);

        this.join = new JButton("参加する");
        this.join.setEnabled(false);
        this.join.setActionCommand("join");
        this.join.addActionListener(this);

        var add = new JButton("部屋を作る");
        add.setActionCommand("create");
        add.addActionListener(this);

        this.refresh = new JButton("更新");
        this.refresh.setActionCommand("refresh");
        this.refresh.addActionListener(this);

        addButton(south, this.join, l, 0, 0, 1, 1, 0.125D);
        addButton(south, this.refresh, l, 0, 1, 1, 1, 0.125D);
        addButton(south, add, l, 0, 4, 1, 1, 0.125D);

        this.add(south, BorderLayout.SOUTH);
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.refreshTicks > 0) {
            this.refreshTicks--;
            if (this.refreshTicks <= 0) {
                this.refresh.setEnabled(true);
            }
        }
    }

    @Nullable
    @Override
    public JMenuBar createMenuBar() {
        var bar = new JMenuBar();
        bar.add(this.createMenuMenu());
        bar.add(this.createNetworkMenu());
        return bar;
    }

    private Optional<RoomInfo> getSelectionOrDialog() {
        var selection = this.list.getSelectedValue();
        if (selection == null) {
            return Optional.empty();
        }

        return Optional.of(selection);
    }

    private void createRoom() {
        this.client.setPanel(new NewRoomPanel(p -> {
            if (!p.isAccepted()) {
                this.client.setPanel(this);
                return;
            }

            this.client.setPanel(new CenteredMessagePanel("部屋を作成しています..."));
            this.client.getConnection().sendPacket(new CreateRoomReq(p.getRoomName(), p.hasPassword() ? p.getPassword() : ""));
        }));
    }

    private void joinRoom() {
        this.getSelectionOrDialog().ifPresent(roomInfo -> {
            this.client.getConnection().sendPacket(new JoinRoomReq(roomInfo.id()));
        });
    }

    private void search() {
        var query = this.searchBox.getText();
        if (query.isEmpty()) {
            return;
        }

        this.getModel().clear();
        this.onRoomListChanged();
        this.search.setEnabled(false);
        this.join.setEnabled(false);
        this.search.setText("検索中...");
        this.client.getConnection().sendPacket(new RoomListQueryReq(query.substring(0, Math.min(query.length(), Room.MAX_ROOM_NAME_LENGTH))));
    }

    private void refresh() {
        this.getModel().clear();
        this.onRoomListChanged();
        this.client.getConnection().sendPacket(new RoomListReq());
        this.join.setEnabled(false);
        this.refresh.setEnabled(false);
        this.refreshTicks = 60;
    }

    public void addAll(List<RoomInfo> infoList) {
        this.getModel().addAll(infoList);
        this.onRoomListChanged();
    }

    private DefaultListModel<RoomInfo> getModel() {
        return (DefaultListModel<RoomInfo>) this.list.getModel();
    }

    public void onRoomListChanged() {
        if (!this.search.isEnabled()) {
            this.search.setEnabled(true);
            this.search.setText("検索");
        }

        this.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "create":
                this.createRoom();
                break;
            case "join":
                this.joinRoom();
                break;
            case "search":
                this.search();
                break;
            case "refresh":
                this.refresh();
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.join.setEnabled(!this.list.isSelectionEmpty());
    }
}
