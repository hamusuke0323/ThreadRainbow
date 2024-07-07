package com.hamusuke.threadr.game.team;

import com.hamusuke.threadr.network.Spider;

import java.util.List;
import java.util.Map;

public abstract class Team {
    public abstract Map<? extends Spider, ? extends TeamEntry> getTeamEntries();

    public abstract List<? extends TeamEntry> getBlueTeam();

    public abstract List<? extends Spider> getBlueSpiders();

    public abstract List<? extends TeamEntry> getRedTeam();

    public abstract List<? extends Spider> getRedSpiders();
}
