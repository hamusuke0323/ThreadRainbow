package com.hamusuke.threadr.server.game.topic;

import com.hamusuke.threadr.game.topic.Topic;
import com.hamusuke.threadr.game.topic.TopicList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ServerTopicList extends TopicList {
    private static final AtomicInteger ID_INCREMENTER = new AtomicInteger();
    private static final Supplier<Integer> INCREMENTED_ID = ID_INCREMENTER::incrementAndGet;

    public ServerTopicList(List<Topic> initialTopics) {
        initialTopics.forEach(topic -> this.addTopicEntry(new TopicEntry(INCREMENTED_ID.get(), topic)));
    }

    public TopicEntry addTopic(Topic topic) {
        return this.addTopicEntry(new TopicEntry(INCREMENTED_ID.get(), topic));
    }
}
