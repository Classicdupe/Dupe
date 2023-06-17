package xyz.prorickey.classicdupe.clans.builders;

import org.bukkit.OfflinePlayer;

import java.util.*;

public class Clan {

    // Clan Stuff
    private final UUID clanId;
    private final String clanName;

    // Players
    private OfflinePlayer owner = null;
    private final List<OfflinePlayer> admins = new ArrayList<>();
    private final List<OfflinePlayer> vips = new ArrayList<>();
    private final List<OfflinePlayer> defaults = new ArrayList<>();
    private final List<OfflinePlayer> members = new ArrayList<>();

    // Settings
    private boolean publicClan = false;
    private String clanColor = "<yellow>";

    // Statistics
    private Integer clanKills;

    // Warps
    private final Map<String, Warp> warps = new HashMap<>();

    public Clan(UUID id, String name) {
        this.clanId = id;
        this.clanName = name;
    }

    public String getClanName() { return this.clanName; }
    public UUID getClanId() { return this.clanId; }

    public OfflinePlayer getOwner() { return this.owner; }
    public List<OfflinePlayer> getAdmins() { return this.admins; }
    public List<OfflinePlayer> getVips() { return this.vips; }
    public List<OfflinePlayer> getDefaults() { return this.defaults; }
    public List<OfflinePlayer> getMembers() { return this.members; }
    public void setOwner(OfflinePlayer offPlayer) {
        this.owner = offPlayer;
    }
    public void addAdmin(OfflinePlayer offPlayer) {
        this.admins.add(offPlayer);
    }
    public void addVip(OfflinePlayer offPlayer) {
        this.vips.add(offPlayer);
    }
    public void addDefault(OfflinePlayer offPlayer) {
        this.defaults.add(offPlayer);
    }
    public void addPlayer(OfflinePlayer offPlayer) {
        this.members.add(offPlayer);
    }
    public void removeAdmin(OfflinePlayer offPlayer) {
        this.admins.remove(offPlayer);
    }
    public void removeVip(OfflinePlayer offPlayer) {
        this.vips.remove(offPlayer);
    }
    public void removeDefault(OfflinePlayer offPlayer) {
        this.defaults.remove(offPlayer);
    }
    public void removePlayer(OfflinePlayer offPlayer) {
        this.defaults.remove(offPlayer);
        this.vips.remove(offPlayer);
        this.admins.remove(offPlayer);
        this.members.remove(offPlayer);
    }

    public void setPublicClan(boolean publicClan) { this.publicClan = publicClan; }
    public void setClanColor(String color) { this.clanColor = color; }
    public boolean getPublicClan() { return this.publicClan; }
    public String getClanColor() { return this.clanColor; }

    public void setWarp(Warp warp) { this.warps.put(warp.name, warp); }
    public void delWarp(String name) { this.warps.remove(name); }
    public Warp getWarp(String name) { return this.warps.get(name); }
    public List<Warp> getWarps() { return this.warps.values().stream().toList(); }
    public List<String> getWarpNames() { return this.warps.keySet().stream().toList(); }

    public void setClanKills(Integer clanKills) { this.clanKills = clanKills; }
    public Integer getClanKills() { return this.clanKills != null ? this.clanKills : 0; }
}
