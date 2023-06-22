package xyz.prorickey.classicdupe;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Component format(String text) { return MiniMessage.miniMessage().deserialize(text); }
    public static Component cmdMsg(String text) {
        return format(Config.getConfig().getString("prefix") + " " + text);
    }

    public static String convertColorCodesToAdventure(String text) {
        final String[] last = {text};
        codeToAdventure.forEach((code, adv) -> last[0] = last[0].replaceAll(code, adv));
        last[0] = translateLegacyHexColorCodes(last[0]);
        return last[0];
    }

    public static String convertAdventureToColorCodes(String text) {
        final String[] last = {text};
        codeToAdventure.forEach((code, adv) -> last[0] = last[0].replaceAll(adv, code));
        return last[0];
    }

    public static final Map<String, String> codeToAdventure = new HashMap<>(){{
        put("&0", "<black>");
        put("&1", "<dark_blue>");
        put("&2", "<dark_green>");
        put("&3", "<dark_aqua>");
        put("&4", "<dark_red>");
        put("&5", "<dark_purple>");
        put("&6", "<gold>");
        put("&7", "<gray>");
        put("&8", "<dark_gray>");
        put("&9", "<blue>");
        put("&a", "<green>");
        put("&b", "<aqua>");
        put("&c", "<red>");
        put("&d", "<light_purple>");
        put("&e", "<yellow>");
        put("&f", "<white>");
        put("&k", "<obf>");
        put("&l", "<b>");
        put("&m", "<st>");
        put("&n", "<u>");
        put("&o", "<i>");
        put("&r", "<reset>");
    }};

    /**
     * Gets the prefix of an offline player.
     * Also removed the boldness from the prefix.
     *
     * @param player The player to get the prefix of
     * @return The prefix of the player formatted with adventure color codes
     */
    @Deprecated
    public static String getPrefix(OfflinePlayer player) {
        String prefix = ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix();
        if(prefix != null) return convertColorCodesToAdventure(prefix) + "<b></b>";
        return "";
    }

    private static String translateLegacyHexColorCodes(String message) {
        Matcher matcher = Pattern.compile("&(#[A-Fa-f0-9]{6})").matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "<color:" + group + ">");
        }
        return matcher.appendTail(buffer).toString();
    }

    public static Integer getMaxHomes(OfflinePlayer player) {
        String rank = ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrimaryGroup();
        if(Config.getConfig().getInt("ranks." + rank + ".homes") != 0) return Config.getConfig().getInt("ranks." + rank + ".homes");
        return 1;
    }

    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) { if (meta.asBoolean()) return true; }
        return false;
    }

    public static String getSuffix(Player player) { return ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getSuffix(); }

    public static String centerText(String text) {
        int maxWidth = 80,
                spaces = (int) Math.round((maxWidth-1.4*text.length())/2);
        return StringUtils.repeat(" ", spaces)+text;
    }

    public static ItemStack getGuiFiller() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        item.editMeta(meta -> meta.displayName(Component.text(" ")));
        return item;
    }
}
