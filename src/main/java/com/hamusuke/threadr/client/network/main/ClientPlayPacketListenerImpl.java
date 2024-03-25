package com.hamusuke.threadr.client.network.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.window.MainWindow;
import com.hamusuke.threadr.client.network.spider.LocalSpider;
import com.hamusuke.threadr.client.network.spider.RemoteSpider;
import com.hamusuke.threadr.game.card.NumberCard;
import com.hamusuke.threadr.game.card.RemoteCard;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.GiveLocalCardS2CPacket;
import com.hamusuke.threadr.network.protocol.packet.s2c.play.RemoteCardGivenS2CPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public MainWindow mainWindow;

    public ClientPlayPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleGiveCard(GiveLocalCardS2CPacket packet) {
        this.clientSpider.takeCard(new NumberCard(this.clientSpider, packet.getNum()));
        this.mainWindow.card();
    }

    @Override
    public void handleRemoteCard(RemoteCardGivenS2CPacket packet) {
        synchronized (this.client.clientSpiders) {
            this.client.clientSpiders.stream().filter(s -> s.getId() == packet.getId()).findFirst().ifPresent(s -> {
                if (s instanceof LocalSpider) {
                    LOGGER.warn("Remote card came for me, should never happen!");
                } else if (s instanceof RemoteSpider spider) {
                    spider.haveRemoteCard(new RemoteCard(spider));
                }
            });
        }
    }
}
