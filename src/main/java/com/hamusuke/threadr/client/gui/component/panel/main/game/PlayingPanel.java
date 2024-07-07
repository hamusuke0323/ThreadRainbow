package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.panel.Panel;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PlayingPanel extends Panel {
    private final Topic topic;

    public PlayingPanel(Topic topic) {
        super(new GridBagLayout());
        this.topic = topic;
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - 「たとえ」て小さい順に並べる " + this.client.getGameTitle());
        var p = this.createGamePanel();
        var l = (GridBagLayout) this.getLayout();
        this.placeComponents(p, l);
    }

    protected void placeComponents(JPanel p, GridBagLayout l) {
        if (this.client.amIHost()) {
            var finish = new JButton("完成！");
            finish.setActionCommand("finish");
            finish.addActionListener(this);
            addButton(this, p, l, 0, 0, 1, 1, 1.0D);
            addButton(this, finish, l, 0, 1, 1, 1, 0.125D);
        } else {
            addButton(this, p, l, 0, 0, 1, 1, 1.0D);
        }
    }

    @Override
    public JPanel createSouth() {
        var layout = new GridBagLayout();
        var south = new JPanel(layout);
        var chatPanel = this.createChatPanel();
        var table = this.createTable();
        addButton(south, this.topic.toPIPPanel(), layout, 0, 0, 1, 1, 1.0D);
        addButton(south, chatPanel, layout, 1, 0, 1, 1, 1.0D);
        addButton(south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        return south;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.FINISH));
    }
}
