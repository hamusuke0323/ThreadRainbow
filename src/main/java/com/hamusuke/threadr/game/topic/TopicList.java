package com.hamusuke.threadr.game.topic;

import com.google.common.collect.Maps;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;

import java.util.*;

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
        public static TopicEntry from(IntelligentByteBuf buf) {
            return new TopicEntry(buf.readVariableInt(), Topic.readFrom(buf));
        }

        public void writeTo(IntelligentByteBuf buf) {
            buf.writeVariableInt(this.id);
            this.topic.writeTo(buf);
        }

        public String toPrettyString() {
            return "ID: %d\n%s\n1 %s %s 100"
                    .formatted(
                            this.id,
                            this.topic.lines().stream()
                                    .reduce((s, s2) -> s + "\n" + s2)
                                    .orElse(""),
                            this.topic.minDescription(),
                            this.topic.maxDescription()
                    );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TopicEntry entry = (TopicEntry) o;
            return id == entry.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}
