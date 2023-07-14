package xyz.prorickey.classicdupe;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.metrics.PlayerMetrics;

import java.util.stream.Collectors;

public class ClassicDupeExpansion extends PlaceholderExpansion {

    public ClassicDupeExpansion(ClassicDupe plugin) {
    }

    @Override
    public @NotNull String getIdentifier() { return "classicdupe"; }

    @Override
    public @NotNull String getAuthor() { return "Prorickey"; }

    @Override
    public @NotNull String getVersion() { return "1.0.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("name")) {
            if(player == null) return null;
            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
            if(ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).getClanID() != null) {
                String clan = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).getClanName();
                String clanColor = ClassicDupe.getClanDatabase().getClan(ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).getClanID()).getClanColor();
                if(data.getNickname()== null) return player.getName() + Utils.convertAdventureToColorCodes(" <dark_gray>[" + clanColor + clan + "<dark_gray>]");
                else return Utils.convertAdventureToColorCodes(data.getNickname()) + Utils.convertAdventureToColorCodes(" <dark_gray>[" + clanColor + clan + "<dark_gray>]");
            }
            if(data.getNickname() == null) return player.getName();
            else return Utils.convertAdventureToColorCodes(data.getNickname());
        }
        if(params.toLowerCase().startsWith("top_kills_")) {
            for (int i = 0; i < 10; i++) {
                if (PlayerDatabase.killsLeaderboard.size() < i + 1) return " ";
                if (params.equalsIgnoreCase("top_kills_" + (i + 1) + "_name"))
                    return PlayerDatabase.killsLeaderboard.get(i + 1);
                else if (params.equalsIgnoreCase("top_kills_" + (i + 1) + "_kills"))
                    return PlayerDatabase.killsLeaderboardK.get(i + 1).toString();
            }
        } else if(params.toLowerCase().startsWith("top_deaths_")) {
            for(int i2 = 0; i2 < 10; i2++) {
                if(PlayerDatabase.deathsLeaderboard.size() < i2+1) return " ";
                if(params.equalsIgnoreCase("top_deaths_" + (i2+1) + "_name")) return PlayerDatabase.deathsLeaderboard.get(i2+1);
                else if(params.equalsIgnoreCase("top_deaths_" + (i2+1) + "_deaths")) return PlayerDatabase.deathsLeaderboardD.get(i2+1).toString();
            }
        } else if(params.toLowerCase().startsWith("top_balance_")) {
            for (int i3 = 0; i3 < 10; i3++) {
                if (PlayerDatabase.balanceTop.size() < i3 + 1) return " ";
                if (params.equalsIgnoreCase("top_balance_" + (i3 + 1) + "_name"))
                    return PlayerDatabase.balanceTop.get(i3 + 1).getName();
                else if (params.equalsIgnoreCase("top_balance_" + (i3 + 1) + "_balance"))
                    return PlayerDatabase.balanceTop.get(i3 + 1).getBalance().toString();
            }
        } else if(params.toLowerCase().startsWith("top_clankills_")) {
            for (int i4 = 0; i4 < 10; i4++) {
                if (ClassicDupe.getClanDatabase().getTopClanKills().size()-1 < i4) return " ";
                Clan clan = ClassicDupe.getClanDatabase().getTopClanKills().get(i4+1);
                if(clan != null) {
                    if (params.equalsIgnoreCase("top_clanKills_" + (i4 + 1) + "_name"))
                        return clan.getClanName();
                    else if (params.equalsIgnoreCase("top_clanKills_" + (i4 + 1) + "_kills"))
                        return clan.getClanKills().toString();
                } else {
                    return " ";
                }
            }
        } else if(params.toLowerCase().startsWith("top_playtime_")) {
            for(int i5 = 0; i5 < 10; i5++) {
                if(PlayerDatabase.playtimeLeaderboard.size() < i5+1) return " ";
                if(params.equalsIgnoreCase("top_playtime_" + (i5+1) + "_name")) return PlayerDatabase.playtimeLeaderboard.get(i5+1);
                else if(params.equalsIgnoreCase("top_playtime_" + (i5+1) + "_playtime")) return PlayerMetrics.getPlaytimeFormatted(PlayerDatabase.playtimeLeaderboardP.get(i5+1));
            }
        } else if(params.toLowerCase().startsWith("top_killstreak_")) {
            for(int i6 = 0; i6 < 10; i6++) {
                if(PlayerDatabase.killStreakLeaderboard.size() < i6+1) return " ";
                if(params.equalsIgnoreCase("top_killstreak_" + (i6+1) + "_name")) return PlayerDatabase.killStreakLeaderboard.get(i6+1);
                else if(params.equalsIgnoreCase("top_killstreak_" + (i6+1) + "_killstreak")) return PlayerDatabase.killStreakLeaderboardK.get(i6+1).toString();
            }
        }
        return null;
    }

}
