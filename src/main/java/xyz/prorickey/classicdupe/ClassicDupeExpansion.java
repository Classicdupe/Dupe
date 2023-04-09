package xyz.prorickey.classicdupe;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.util.HashMap;
import java.util.Map;

public class ClassicDupeExpansion extends PlaceholderExpansion {

    private final ClassicDupe plugin;

    public ClassicDupeExpansion(ClassicDupe plugin) {
        this.plugin = plugin;
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
            PlayerDatabase.PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(player.getUniqueId().toString());
            String clan = ClanDatabase.getClanMember(player.getUniqueId()).getClanName();
            if(data.nickname == null) return (clan != null ? clan + " " + player.getName() : player.getName());
            else return (clan != null ? clan + " " + data.nickname : data.nickname);
        }
        for(int i = 0; i < 10; i++) {
            if(ClassicDupe.getDatabase().getPlayerDatabase().killsLeaderboard.size() < i+1) return " ";
            if(params.equalsIgnoreCase("top_kills_" + (i+1) + "_name")) return ClassicDupe.getDatabase().getPlayerDatabase().killsLeaderboard.get(i+1);
            else if(params.equalsIgnoreCase("top_kills_" + (i+1) + "_kills")) return ClassicDupe.getDatabase().getPlayerDatabase().killsLeaderboardK.get(i+1).toString();
        }
        for(int i = 0; i < 10; i++) {
            if(ClassicDupe.getDatabase().getPlayerDatabase().deathsLeaderboard.size() < i+1) return " ";
            if(params.equalsIgnoreCase("top_deaths_" + (i+1) + "_name")) return ClassicDupe.getDatabase().getPlayerDatabase().deathsLeaderboard.get(i+1);
            else if(params.equalsIgnoreCase("top_deaths_" + (i+1) + "_deaths")) return ClassicDupe.getDatabase().getPlayerDatabase().deathsLeaderboardD.get(i+1).toString();
        }
        return null;
    }

}
