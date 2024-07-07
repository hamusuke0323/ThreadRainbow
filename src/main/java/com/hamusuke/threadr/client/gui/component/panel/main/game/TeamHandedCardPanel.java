package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TeamHandedCardPanel extends HandedCardPanel {
    @Override
    public JMenuBar createMenuBar() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.setPanel(new CheckingNumberPanel() {
            @Override
            public void actionPerformed(ActionEvent e) {
                this.client.setPanel(new WaitingHostPanel("ゲームを始める", Command.START_MAIN_GAME));
            }
        });
        SwingUtilities.invokeLater(this.client.spiderTable::addCardNumCol);
    }
}
