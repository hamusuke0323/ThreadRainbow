package com.hamusuke.threadr.client.gui.window;

import com.hamusuke.threadr.client.gui.dialog.ConfirmDialog;
import com.hamusuke.threadr.client.gui.dialog.DedicatedConnectingDialog;
import com.hamusuke.threadr.client.gui.dialog.OkDialog;
import com.hamusuke.threadr.client.gui.dialog.ServerInfoDialog;
import com.hamusuke.threadr.network.ServerInfo;
import com.hamusuke.threadr.network.ServerInfo.Status;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class ServerListWindow extends Window {
    private final String msg;
    private JList<ServerInfo> list;
    private JButton refresh;
    private int refreshBtnTicks;

    public ServerListWindow(String msg) {
        super("サーバー一覧");

        this.msg = msg;
    }

    public ServerListWindow() {
        this("");
    }

    @Override
    public void init() {
        super.init();

        if (!this.msg.isEmpty()) {
            new SwingWorker<>() {
                @Override
                protected Object doInBackground() {
                    new OkDialog(ServerListWindow.this, "エラー", msg);
                    return null;
                }
            }.execute();
        }

        var model = new DefaultListModel<ServerInfo>();
        model.addAll(this.client.getServers());
        this.list = new JList<>(model);
        if (model.getSize() > 0) {
            this.list.setSelectedIndex(0);
        }
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setOpaque(true);
        this.list.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            var panel = new JPanel();
            panel.setBackground(isSelected ? this.list.getSelectionBackground() : Color.WHITE);
            panel.setBorder(new LineBorder(Color.BLACK, 1));
            panel.add(label);
            return panel;
        });
        this.add(new JScrollPane(this.list), BorderLayout.CENTER);

        var l = new GridBagLayout();
        var south = new JPanel(l);

        var con = new JButton("接続");
        con.setActionCommand("con");
        con.addActionListener(this);
        var rm = new JButton("削除");
        rm.setActionCommand("rm");
        rm.addActionListener(this);
        var add = new JButton("サーバーを追加");
        add.setActionCommand("add");
        add.addActionListener(this);
        var edit = new JButton("編集");
        edit.setActionCommand("edit");
        edit.addActionListener(this);
        this.refresh = new JButton("更新");
        this.refresh.setActionCommand("refresh");
        this.refresh.addActionListener(this);

        addButton(south, con, l, 0, 0, 1, 1, 0.125D);
        addButton(south, this.refresh, l, 0, 1, 1, 1, 0.125D);
        addButton(south, edit, l, 0, 2, 1, 1, 0.125D);
        addButton(south, rm, l, 0, 3, 1, 1, 0.125D);
        addButton(south, add, l, 0, 4, 1, 1, 0.125D);

        this.add(south, BorderLayout.SOUTH);
        this.pack();
        this.setSize(new Dimension(this.getWidth() * 2, this.getHeight()));
        this.setLocationRelativeTo(null);
    }

    @Override
    public void tick() {
        if (this.refreshBtnTicks > 0) {
            this.refreshBtnTicks--;
            if (this.refreshBtnTicks <= 0) {
                this.refresh.setEnabled(true);
            }
        }
    }

    @Override
    protected void onClose() {
        this.client.stopLooping();
    }

    private void connect() {
        this.getSelectionOrDialog().ifPresent(info -> new DedicatedConnectingDialog(this, this.client, info.address, info.port));
    }

    private void remove() {
        this.getSelectionOrDialog().ifPresent(info -> new ConfirmDialog(this, "削除してもよろしいですか", b -> {
            if (b) {
                this.client.removeServer(info);
                this.client.saveServers();
                ((DefaultListModel<ServerInfo>) this.list.getModel()).removeElement(info);
            }
        }));
    }

    private void add() {
        new ServerInfoDialog(this, d -> {
            var info = new ServerInfo(d.getAddress(), d.getPort());
            if (this.client.addServer(info)) {
                this.client.saveServers();
                ((DefaultListModel<ServerInfo>) this.list.getModel()).addElement(info);
            }
        });
    }

    private void edit() {
        this.getSelectionOrDialog().ifPresent(info -> {
            new ServerInfoDialog(this, d -> {
                var newInfo = new ServerInfo(d.getAddress(), d.getPort());
                if (!info.equals(newInfo) && this.client.getServers().contains(newInfo)) {
                    this.client.removeServer(info);
                    ((DefaultListModel<ServerInfo>) this.list.getModel()).removeElement(info);
                } else {
                    info.address = newInfo.address;
                    info.port = newInfo.port;
                }

                this.client.saveServers();
                this.list.repaint();
            }, info);
        });
    }

    private Optional<ServerInfo> getSelectionOrDialog() {
        var selection = this.list.getSelectedValue();
        if (selection == null) {
            new OkDialog(this, "エラー", "サーバーが選択されていません");
            return Optional.empty();
        }

        return Optional.of(selection);
    }

    private void refresh() {
        this.getSelectionOrDialog().ifPresent(info -> {
            if (info.status == Status.CONNECTING) {
                new OkDialog(this, "エラー", "まだ更新中です");
                return;
            }

            this.client.checkServerInfo(info);
            this.refresh.setEnabled(false);
            this.refreshBtnTicks = 40;
        });
    }

    public void onServerInfoChanged() {
        this.list.repaint();
        this.list.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "con":
                this.connect();
                break;
            case "rm":
                this.remove();
                break;
            case "add":
                this.add();
                break;
            case "edit":
                this.edit();
                break;
            case "refresh":
                this.refresh();
                break;
        }
    }
}
