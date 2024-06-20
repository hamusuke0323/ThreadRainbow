package com.hamusuke.threadr.game.topic;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class TopicList {
    private final Map<Integer, TopicEntry> topics = Maps.newConcurrentMap();
    private final Map<Integer, TopicEntry> topicMap = Collections.unmodifiableMap(this.topics);

    public synchronized TopicEntry addTopicEntry(TopicEntry entry) {
        this.topics.put(entry.id, entry);
        return entry;
    }

    public synchronized Optional<TopicEntry> removeTopicEntry(int id) {
        return Optional.ofNullable(this.topics.remove(id));
    }

    public Map<Integer, TopicEntry> getTopics() {
        return this.topicMap;
    }

    public List<TopicEntry> getTopicEntries() {
        return List.copyOf(this.topicMap.values());
    }

    public record TopicEntry(int id, Topic topic) {
    }
}
