package xyz.prorickey.classicdupe;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;
import xyz.prorickey.proutils.ChatFormat;

public class Utils {

    public static String cmdMsg(String text) { return ChatFormat.format(Config.getConfig().getString("prefix") + " &f" + text); }

    public static String getPrefix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix(); }
    public static String getSuffix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getSuffix(); }

    public static String centerText(String text) {
        int maxWidth = 80,
                spaces = (int) Math.round((maxWidth-1.4*ChatColor.stripColor(text).length())/2);
        return StringUtils.repeat(" ", spaces)+text;
    }
}
