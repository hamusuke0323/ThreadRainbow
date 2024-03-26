package com.hamusuke.threadr.game.topic;

import com.google.common.collect.ImmutableList;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;

import java.util.List;

public record Topic(List<String> lines, String minDescription, String maxDescription) {
    public static Topic readFrom(IntelligentByteBuf buf) {
        return new Topic(buf.readList(IntelligentByteBuf::readString, ImmutableList::copyOf), buf.readString(), buf.readString());
    }

    public void writeTo(IntelligentByteBuf buf) {
        buf.writeList(this.lines, (s, buf1) -> buf1.writeString(s));
        buf.writeString(this.minDescription);
        buf.writeString(this.maxDescription);
    }

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
