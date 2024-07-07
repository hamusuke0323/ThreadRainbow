package com.hamusuke.threadr.client.gui.component.panel.main.game;

import com.hamusuke.threadr.game.mode.ThreadRainbowGame;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.play.ClientCommandReq.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TeamPlayingPanel extends PlayingPanel {
    private final JButton finish;
    private final JLabel timerLabel;
    private boolean finishBtnPressed;
    private boolean timerStarted;
    private int timer;

    public TeamPlayingPanel(Topic topic) {
        super(topic);

        this.finish = new JButton("完成！");
        this.finish.addActionListener(this);
        this.timerLabel = new JLabel();
        this.timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.timerStarted && this.timer > 0) {
            this.timer--;

            if (this.timer % 20 == 0) {
                this.updateTimer();
            }
        }
    }

    @Override
    protected void placeComponents(JPanel p, GridBagLayout l) {
        addButton(this, p, l, 0, 1, 1, 1, 1.0D);

        if (!this.finishBtnPressed) {
            addButton(this, this.finish, l, 0, 2, 1, 1, 0.125D);
        }

        this.placeTimer();
    }

    public void removeFinishBtn() {
        if (this.finishBtnPressed) {
            return;
        }

        this.finishBtnPressed = true;
        this.finish.setVisible(false);
        this.finish.setEnabled(false);
        this.remove(this.finish);
    }

    public void startTimer() {
        this.timer = ThreadRainbowGame.ONE_MINUTE_TICKS;
        this.timerStarted = true;
    }

    public void placeTimer() {
        if (!this.timerStarted) {
            return;
        }

        addButton(this, this.timerLabel, (GridBagLayout) this.getLayout(), 0, 0, 1, 1, 0.0125D);
    }

    private void updateTimer() {
        this.timerLabel.setText("残り " + this.timer / 20 + " 秒");
    }

    public void syncTimerWithServer(int serverSideTicks) {
        this.timer = serverSideTicks;
        this.updateTimer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.TEAM_FINISH));
    }
}
