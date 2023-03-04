package xyz.prorickey.classicdupe;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static xyz.prorickey.classicdupe.Utils.TabCompleteType.*;

public class Utils {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public static String cmdMsg(String text) { return Utils.format(Config.getConfig().getString("prefix") + " &f" + text); }

    public static String getPrefix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix(); }
    public static String getSuffix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getSuffix(); }

    public enum TabCompleteType {
        CONTAINS,
        SEARCH,
        NONE
    }

    public static <TabCompleteType> List<String> tabCompletions(TabCompleteType type, String currentArg, List<String> completions) {
        if (currentArg.length() <= 0) { return completions; }
        currentArg = currentArg.toLowerCase(Locale.ROOT);
        List<String> returnedCompletions = new ArrayList<>();
        if (CONTAINS.equals(type)) {
            for (String str : completions) {
                if (str.toLowerCase(Locale.ROOT).contains(currentArg)) {
                    returnedCompletions.add(str);
                }
            }
        } else if (SEARCH.equals(type)) {
            for (String str : completions) {
                if (str.toLowerCase(Locale.ROOT).startsWith(currentArg)) {
                    returnedCompletions.add(str);
                }
            }
        } else if (NONE.equals(type)) {
            returnedCompletions.addAll(completions);
        }
        return returnedCompletions;
    }

    public static List<String> tabCompletionsContains(String currentArg, List<String> completions) { return tabCompletions(TabCompleteType.CONTAINS, currentArg, completions); }
    public static List<String> tabCompletionsSearch(String currentArg, List<String> completions) { return tabCompletions(TabCompleteType.SEARCH, currentArg, completions); }

    public static String centerText(String text) {
        int maxWidth = 80,
                spaces = (int) Math.round((maxWidth-1.4*ChatColor.stripColor(text).length())/2);
        return StringUtils.repeat(" ", spaces)+text;
    }
}
