package com.hamusuke.threadr.client.gui.component.list;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.dialog.ConfirmPanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.NewTopicPanel;
import com.hamusuke.threadr.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.game.topic.TopicList.TopicEntry;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.CreateTopicReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.RemoveTopicReq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.hamusuke.threadr.client.gui.component.panel.Panel.addButton;

public class TopicListPanel extends JPanel implements ActionListener {
    private final ThreadRainbowClient client;
    private final JTopicList topicList;
    public final JScrollPane topicScroll;
    private final JButton mkTopic;
    private final JButton rmTopic;

    public TopicListPanel(ThreadRainbowClient client, JTopicList topicList) {
        super(new GridBagLayout());
        this.client = client;
        this.topicList = topicList;
        var grid = (GridBagLayout) this.getLayout();
        this.topicScroll = new JScrollPane(topicList);

        this.mkTopic = new JButton("お題を作成");
        this.mkTopic.setEnabled(false);
        this.mkTopic.addActionListener(this);
        this.mkTopic.setActionCommand("mkTopic");

        this.rmTopic = new JButton("お題を削除");
        this.rmTopic.setEnabled(false);
        this.rmTopic.addActionListener(this);
        this.rmTopic.setActionCommand("rmTopic");

        addButton(this, this.topicScroll, grid, 0, 0, 1, 1, 1.0D);
    }

    public void showButtons() {
        if (!this.client.amIHost() || this.mkTopic.isEnabled() || this.rmTopic.isEnabled()) {
            return;
        }

        this.mkTopic.setEnabled(true);
        this.rmTopic.setEnabled(true);
        var grid = (GridBagLayout) this.getLayout();
        addButton(this, this.mkTopic, grid, 0, 1, 1, 1, 0.125D);
        addButton(this, this.rmTopic, grid, 0, 2, 1, 1, 0.125D);
        this.revalidate();
    }

    public void hideButtons() {
        this.mkTopic.setEnabled(false);
        this.rmTopic.setEnabled(false);
        this.remove(this.mkTopic);
        this.remove(this.rmTopic);
        this.revalidate();
    }

    private void createTopic() {
        var curPanel = this.client.getPanel();
        this.hideButtons();
        this.client.setPanel(new NewTopicPanel(p -> {
            this.client.setPanel(curPanel);
            this.showButtons();

            if (this.client.getConnection() == null) {
                return;
            }

            List<String> lines = p.hasSubText() ? Lists.newArrayList(p.getSubText(), p.getMainText()) : Lists.newArrayList(p.getMainText());
            this.client.getConnection().sendPacket(new CreateTopicReq(new Topic(lines, p.getMinDesc(), p.getMaxDesc())));
        }));
    }

    private void removeTopic() {
        this.hideButtons();

        if (this.client.topics.getSelectedValuesList().isEmpty()) {
            this.client.setPanel(new OkPanel(this.client.getPanel(), "エラー", "お題が選択されていません") {
                @Override
                public void onClose() {
                    super.onClose();
                    TopicListPanel.this.showButtons();
                }
            });
            return;
        }

        this.client.setPanel(new ConfirmPanel(this.client.getPanel(), "お題を削除してもよろしいですか？", y -> {
            if (y && this.client.getConnection() != null) {
                this.client.getConnection().sendPacket(new RemoveTopicReq(this.client.topics.getSelectedValuesList().stream().map(TopicEntry::id).toList()));
            }

            this.showButtons();
        }));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "mkTopic":
                this.createTopic();
                break;
            case "rmTopic":
                this.removeTopic();
                break;
        }
    }
}
