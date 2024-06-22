package com.hamusuke.threadr.client.game.topic;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.game.topic.TopicList;

import java.util.List;

public class ClientTopicList extends TopicList {
    private final ThreadRainbowClient client;

    public ClientTopicList(ThreadRainbowClient client) {
        this.client = client;
    }

    public synchronized void syncWithServer(List<TopicEntry> topicEntries) {
        topicEntries.forEach(this::addTopicEntry);
        this.client.topics.addTopics(topicEntries);
    }

    public synchronized void removeTopics(List<Integer> removed) {
        removed.forEach(integer -> this.removeTopicEntry(integer)
                .ifPresent(this.client.topics::removeTopic));
    }
}
