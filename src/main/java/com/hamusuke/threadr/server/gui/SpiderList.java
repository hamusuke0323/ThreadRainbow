package com.hamusuke.threadr.server.gui;

import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.server.ThreadRainbowServer;

import javax.swing.*;
import java.util.Vector;

public class SpiderList extends JList<String> {
    private final ThreadRainbowServer server;
    private int tickCount;

    public SpiderList(ThreadRainbowServer server) {
        this.server = server;
        server.addTickable(this::tick);
    }

    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            this.setListData(
                    new Vector<>(
                            this.server.getSpiderManager().getSpiders().stream()
                                    .map(Spider::getName)
                                    .toList()));
        }
    }
}
