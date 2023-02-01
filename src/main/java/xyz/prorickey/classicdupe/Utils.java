package xyz.prorickey.classicdupe;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getPrefix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix(); }
    public static String getSuffix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getSuffix(); }

}
