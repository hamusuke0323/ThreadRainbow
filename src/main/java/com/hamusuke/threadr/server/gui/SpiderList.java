package com.hamusuke.threadr.server.gui;

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
            var vector = new Vector<String>();

            for (int i = 0; i < this.server.getSpiderManager().getSpiders().size(); i++) {
                vector.add(this.server.getSpiderManager().getSpiders().get(i).getName());
            }

            this.setListData(vector);
        }
    }
}
