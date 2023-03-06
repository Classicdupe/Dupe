package xyz.prorickey.classicdupe;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

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
            if(data.nickname == null) return player.getName();
            else return data.nickname;
        }
        return null;
    }
}
