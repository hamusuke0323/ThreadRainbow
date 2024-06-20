package com.hamusuke.threadr.client.game.topic;

import com.hamusuke.threadr.game.topic.TopicList;

import java.util.List;

public class ClientTopicList extends TopicList {
    public synchronized void syncWithServer(List<TopicEntry> topicEntries) {
        topicEntries.forEach(this::addTopicEntry);
    }
}
