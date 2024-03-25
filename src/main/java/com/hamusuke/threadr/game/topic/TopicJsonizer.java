package com.hamusuke.threadr.game.topic;

import com.google.common.collect.Lists;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TopicJsonizer {
    public static void main(String[] args) throws Exception {
    }

    private static List<Topic> getTopics(InputStream is) {
        var scanner = new Scanner(is, StandardCharsets.UTF_8);
        List<Topic> topics = Lists.newArrayList();
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            var topic = line.substring(0, line.lastIndexOf('（'));
            var min = line.substring(line.indexOf(':') + 1, line.indexOf('-'));
            var max = line.substring(line.lastIndexOf(':') + 1, line.lastIndexOf('）'));
            topics.add(new Topic(Arrays.asList(topic.split(" ")), min, max));
        }

        return topics;
    }
}
