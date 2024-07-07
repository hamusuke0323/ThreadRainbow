package com.hamusuke.threadr.server.game.team;

import com.hamusuke.threadr.game.team.TeamEntry;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.util.function.Function;

public class ServerTeamEntry extends TeamEntry {
    public ServerTeamEntry(ServerSpider spider) {
        super(spider);
    }

    public static ServerTeamEntry deserializeForServer(TeamSerializer serializer, Function<Integer, ServerSpider> spiderGetter) {
        var e = new ServerTeamEntry(spiderGetter.apply(serializer.spiderId()));
        e.changeTeam(serializer.type());
        return e;
    }

    @Override
    public ServerSpider getSpider() {
        return (ServerSpider) this.spider;
    }
}
