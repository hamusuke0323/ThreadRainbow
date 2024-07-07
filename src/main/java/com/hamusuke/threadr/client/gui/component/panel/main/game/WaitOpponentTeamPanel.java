package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.client.gui.component.list.NumberCardList;
import com.hamusuke.threadr.client.gui.component.panel.Panel;

import java.awt.*;

public class WaitOpponentTeamPanel extends Panel {
    public WaitOpponentTeamPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ゲーム - 他のチームが完成するのを待つ " + this.client.getGameTitle());
        var p = this.createGamePanel();
        var l = (GridBagLayout) this.getLayout();
        addButton(this, p, l, 0, 0, 1, 1, 1.0D);
    }

    @Override
    protected NumberCardList createNumberCardList() {
        var list = NumberCardList.result(this.client);
        list.setModel(this.client.model);
        return list;
    }
}
