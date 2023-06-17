package xyz.prorickey.classicdupe;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

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
        for(int i = 0; i < 10; i++) {
            if(PlayerDatabase.killsLeaderboard.size() < i+1) return " ";
            if(params.equalsIgnoreCase("top_kills_" + (i+1) + "_name")) return PlayerDatabase.killsLeaderboard.get(i+1);
            else if(params.equalsIgnoreCase("top_kills_" + (i+1) + "_kills")) return PlayerDatabase.killsLeaderboardK.get(i+1).toString();
        }
        for(int i = 0; i < 10; i++) {
            if(PlayerDatabase.deathsLeaderboard.size() < i+1) return " ";
            if(params.equalsIgnoreCase("top_deaths_" + (i+1) + "_name")) return PlayerDatabase.deathsLeaderboard.get(i+1);
            else if(params.equalsIgnoreCase("top_deaths_" + (i+1) + "_deaths")) return PlayerDatabase.deathsLeaderboardD.get(i+1).toString();
        }
        for(int i = 0; i < 10; i++) {
            if(PlayerDatabase.deathsLeaderboard.size() < i+1) return " ";
            if(params.equalsIgnoreCase("top_balance_" + (i+1) + "_name")) return PlayerDatabase.balanceTop.get(i+1).getName();
            else if(params.equalsIgnoreCase("top_balance_" + (i+1) + "_balance")) return PlayerDatabase.balanceTop.get(i+1).getBalance().toString();
        }
        return null;
    }

}
