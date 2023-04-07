package xyz.prorickey.classicdupe.clans;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ClanMember {

    private OfflinePlayer offPlayer;
    private String clanName = null;
    private UUID clanId = null;
    private Integer level = null;

    public ClanMember(
            OfflinePlayer offPlayer
    ) {
        this.offPlayer = offPlayer;
    }

    public void setClan(Clan clan, Integer level) {
        this.clanId = clan.getClanId();
        this.clanName = clan.getClanName();
        this.level = level;
    }

    public void removeClan() {
        this.clanId = null;
        this.clanName = null;
        this.level = null;
    }

    public UUID getClanID() { return this.clanId; }
    public String getClanName() { return this.clanName; }
    public Integer getLevel() { return this.level; }

}
