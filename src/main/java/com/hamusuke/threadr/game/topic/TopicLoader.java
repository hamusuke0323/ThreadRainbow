package com.hamusuke.threadr.game.topic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.hamusuke.threadr.util.throwables.NoAvailableTopicException;

import java.io.InputStreamReader;
import java.util.List;

public class TopicLoader {
    private static final String FILE = "/topics.json";
    private static final Gson GSON = new Gson();
    private final List<Topic> topics = Lists.newArrayList();

    public synchronized void loadTopics() {
        this.topics.clear();
        try (var is = TopicJsonizer.class.getResourceAsStream(FILE)) {
            if (is == null) {
                throw new NoAvailableTopicException("probably " + FILE + " does not exist?");
            }

            GSON.fromJson(new InputStreamReader(is), JsonArray.class).forEach(e -> this.topics.add(GSON.fromJson(e, Topic.class)));
        } catch (Exception e) {
            throw new NoAvailableTopicException("Error occurred while loading topics", e);
        }
    }

    public List<Topic> getTopics() {
        return ImmutableList.copyOf(this.topics);
    }
}
