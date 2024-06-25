package com.hamusuke.threadr.server.game.topic;

import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.game.topic.TopicList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ServerTopicList extends TopicList {
    private final AtomicInteger idIncrementer = new AtomicInteger();
    private final Supplier<Integer> incrementedId = this.idIncrementer::incrementAndGet;

    public ServerTopicList(List<Topic> initialTopics) {
        initialTopics.forEach(topic -> this.addTopicEntry(new TopicEntry(this.incrementedId.get(), topic)));
    }

    public TopicEntry addTopic(Topic topic) {
        return this.addTopicEntry(new TopicEntry(this.incrementedId.get(), topic));
    }
}
