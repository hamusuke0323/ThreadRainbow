package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.list.TeamList;
import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MakingTeamPanel extends Panel {
    private final JScrollPane teamListScroll;

    public MakingTeamPanel(TeamList teamList) {
        super(new GridBagLayout());
        this.teamListScroll = new JScrollPane(teamList);
    }

    @Override
    public void init() {
        super.init();

        var grid = (GridBagLayout) this.getLayout();
        addButton(this, new JLabel("ホストは名前をクリックしてチーム分けしてください", SwingConstants.CENTER), grid, 0, 0, 1, 1, 0.0125D);
        addButton(this, this.teamListScroll, grid, 0, 1, 1, 1, 1.0D);

        if (this.client.amIHost()) {
            var done = new JButton("完了");
            done.setActionCommand("done");
            done.addActionListener(this);
            addButton(this, done, grid, 0, 2, 1, 1, 0.125D);
        }
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        jMenuBar.add(this.createMenuMenu());
        jMenuBar.add(this.createChatMenu());
        jMenuBar.add(this.createNetworkMenu());
        jMenuBar.add(this.createTopicMenu());
        return jMenuBar;
    }

    @Override
    protected JMenu createMenuMenu() {
        var menu = super.createMenuMenu();
        var exit = new JMenuItem("ゲームをやめる");
        exit.setActionCommand("exit");
        exit.addActionListener(this.client.getMainWindow());
        var leave = new JMenuItem("部屋から退出");
        leave.setActionCommand("leave");
        leave.addActionListener(this.client.getMainWindow());
        menu.insert(exit, 0);
        menu.insert(leave, 1);
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.FINISH_MAKING_TEAM));
    }
}
