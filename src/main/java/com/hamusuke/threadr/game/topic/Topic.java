package com.hamusuke.threadr.game.topic;

import java.util.List;

public record Topic(List<String> lines, String minDescription, String maxDescription) {
    @Override
    public String toString() {
        var builder = new StringBuilder("lines: [");
        var it = this.lines.iterator();
        while (it.hasNext()) {
            builder.append('"').append(it.next()).append('"');

            if (it.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.append("], minDescription: \"").append(this.minDescription).append('"').append(", maxDescription: \"").append(this.maxDescription).append('"').toString();
    }
}
