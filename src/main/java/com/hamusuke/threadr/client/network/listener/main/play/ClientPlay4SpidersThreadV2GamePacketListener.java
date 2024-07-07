package com.hamusuke.threadr.client.network.listener.main.play;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.main.game.HandedCardPanel;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.LocalCardHandedNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.play.TopicChangeNotify;

import javax.swing.*;

public class ClientPlay4SpidersThreadV2GamePacketListener extends ClientPlayPacketListenerImpl {
    public ClientPlay4SpidersThreadV2GamePacketListener(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
    }

    @Override
    public void handleGiveCard(LocalCardHandedNotify packet) {
        super.handleGiveCard(packet);
        this.client.setPanel(new HandedCardPanel());
    }

    @Override
    public void handleSelectTopic(TopicChangeNotify packet) {
        super.handleSelectTopic(packet);
        SwingUtilities.invokeLater(this.client.spiderTable::addCardNumCol);
    }
}
