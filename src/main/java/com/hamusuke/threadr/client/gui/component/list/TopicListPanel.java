package com.hamusuke.threadr.client.gui.component.list;

import javax.swing.*;
import java.awt.*;

public class TopicListPanel extends JPanel {
    public final JScrollPane topicScroll;

    public TopicListPanel(JTopicList topicList) {
        super(new BorderLayout());

        this.topicScroll = new JScrollPane(topicList);
        this.add(this.topicScroll, BorderLayout.CENTER);
    }
}
