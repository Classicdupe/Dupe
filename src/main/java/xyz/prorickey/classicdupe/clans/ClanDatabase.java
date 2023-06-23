package xyz.prorickey.classicdupe.clans;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.Warp;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ClanDatabase {

    YamlConfiguration getClanConfig();

    UUID generateClanId();
    void createClan(String clanName, Player player);
    void deleteClan(Clan clan);

    List<String> getLoadedClanNames();
    Clan getClan(UUID id);
    Clan getClan(String clanName);

    ClanMember getClanMember(UUID id);
    void updateClanMemberInfo(Player player);
    void setClan(UUID uuid, Clan clan);
    void removeClan(ClanMember clanMember);

    void setClanColor(Clan clan, String color);
    void setPublicClan(Clan clan, boolean isPublic);
    void setWarp(Clan clan, Warp warp);
    void delWarp(Clan clan, String warpName);

    void setPlayerLevel(ClanMember clanMember, int level);

    void putInClanChat(Player player);
    void removeFromClanChat(Player player);
    boolean clanChat(Player player);
    void sendClanChat(String message, Player player);

    void addClanKill(Clan clan);
    Map<Integer, Clan> getTopClanKills();

}
