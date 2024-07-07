package com.hamusuke.threadr.server.game.team;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hamusuke.threadr.game.team.Team;
import com.hamusuke.threadr.game.team.TeamEntry.TeamType;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.server.network.ServerSpider;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerTeam extends Team {
    private final Map<ServerSpider, ServerTeamEntry> teamEntries = Maps.newConcurrentMap();
    private final Set<Integer> blueFinishBtnPressers = Sets.newConcurrentHashSet();
    private final Set<Integer> redFinishBtnPressers = Sets.newConcurrentHashSet();

    public ServerTeam(List<ServerSpider> spiders) {
        spiders.forEach(s -> this.teamEntries.put(s, new ServerTeamEntry(s)));
    }

    @Override
    public Map<ServerSpider, ServerTeamEntry> getTeamEntries() {
        return this.teamEntries;
    }

    @Override
    public List<ServerTeamEntry> getBlueTeam() {
        return this.teamEntries.values().stream().filter(e -> e.getType() == TeamType.BLUE).toList();
    }

    @Override
    public List<ServerSpider> getBlueSpiders() {
        return this.getBlueTeam().stream().map(ServerTeamEntry::getSpider).toList();
    }

    public void sendPacketToBlueTeam(Packet<?> packet) {
        this.getBlueSpiders().forEach(s -> s.sendPacket(packet));
    }

    public void sendPacketToOthersInBlueTeam(ServerSpider sender, Packet<?> packet) {
        this.getBlueSpiders().stream().filter(spider -> spider != sender).forEach(spider -> spider.sendPacket(packet));
    }

    @Override
    public List<ServerTeamEntry> getRedTeam() {
        return this.teamEntries.values().stream().filter(e -> e.getType() == TeamType.RED).toList();
    }

    @Override
    public List<ServerSpider> getRedSpiders() {
        return this.getRedTeam().stream().map(ServerTeamEntry::getSpider).toList();
    }

    public void sendPacketToRedTeam(Packet<?> packet) {
        this.getRedSpiders().forEach(s -> s.sendPacket(packet));
    }

    public void sendPacketToOthersInRedTeam(ServerSpider sender, Packet<?> packet) {
        this.getRedSpiders().stream().filter(spider -> spider != sender).forEach(s -> s.sendPacket(packet));
    }

    public void pressFinishBtn(TeamType teamType, ServerSpider spider) {
        var set = switch (teamType) {
            case BLUE -> this.blueFinishBtnPressers;
            case RED -> this.redFinishBtnPressers;
        };
        set.add(spider.getId());
    }

    public boolean isBlueTeamFinished() {
        return this.blueFinishBtnPressers.size() >= this.getBlueSpiders().size();
    }

    public boolean isRedTeamFinished() {
        return this.redFinishBtnPressers.size() >= this.getRedSpiders().size();
    }

    public ServerTeamEntry removeSpider(ServerSpider spider) {
        var e = this.teamEntries.remove(spider);
        (switch (e.getType()) {
            case BLUE -> this.blueFinishBtnPressers;
            case RED -> this.redFinishBtnPressers;
        }).remove(spider.getId());
        return e;
    }

    public void reset() {
        this.blueFinishBtnPressers.clear();
        this.redFinishBtnPressers.clear();
    }
}
