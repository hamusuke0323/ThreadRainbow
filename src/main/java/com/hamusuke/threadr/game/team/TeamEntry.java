package com.hamusuke.threadr.game.team;

import com.hamusuke.threadr.network.Spider;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;

import java.awt.*;
import java.util.function.Function;

public class TeamEntry {
    protected final Spider spider;
    protected TeamType type = TeamType.BLUE;

    public TeamEntry(Spider spider) {
        this.spider = spider;
    }

    public static TeamEntry deserialize(TeamSerializer serializer, Function<Integer, ? extends Spider> spiderGetter) {
        var e = new TeamEntry(spiderGetter.apply(serializer.spiderId));
        e.changeTeam(serializer.type);
        return e;
    }

    public TeamSerializer toSerializer() {
        return new TeamSerializer(this.type, this.spider.getId());
    }

    public void changeTeam(TeamType type) {
        this.type = type;
    }

    public void toggleTeam() {
        this.changeTeam(this.type.opposite());
    }

    public TeamType getType() {
        return this.type;
    }

    public Spider getSpider() {
        return this.spider;
    }

    public enum TeamType {
        BLUE(Color.BLUE),
        RED(Color.RED);

        private final Color color;

        TeamType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return this.color;
        }

        public TeamType opposite() {
            return this == BLUE ? RED : BLUE;
        }
    }

    public record TeamSerializer(TeamType type, int spiderId) {
        public static TeamSerializer from(IntelligentByteBuf buf) {
            return new TeamSerializer(buf.readEnum(TeamType.class), buf.readVariableInt());
        }

        public void writeTo(IntelligentByteBuf buf) {
            buf.writeEnum(this.type);
            buf.writeVariableInt(this.spiderId);
        }
    }
}
