package xyz.prorickey.classicdupe.clans.builders;

import org.bukkit.OfflinePlayer;
import xyz.prorickey.classicdupe.clans.builders.Clan;

import java.util.UUID;

public class ClanMember {

    private final OfflinePlayer offPlayer;
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

    public OfflinePlayer getOffPlayer() { return this.offPlayer; }
    public UUID getClanID() { return this.clanId; }
    public String getClanName() { return this.clanName; }
    public Integer getLevel() { return this.level; }

    public void setLevel(Integer level) { this.level = level; }

}
