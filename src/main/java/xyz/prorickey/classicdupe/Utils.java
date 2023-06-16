package xyz.prorickey.classicdupe;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    public static Component format(String text) { return MiniMessage.miniMessage().deserialize(text); }
    public static Component cmdMsg(String text) {
        return format(Config.getConfig().getString("prefix") + " " + text);
    }

    public static String convertColorCodesToAdventure(String text) {
        final String[] last = {text};
        codeToAdventure.forEach((code, adv) -> last[0] = last[0].replaceAll(code, adv));
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

    public static String getPrefix(OfflinePlayer player) {
        String rank = ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrimaryGroup();
        if(Config.getConfig().getString("ranks." + rank + ".prefix") != null) return Config.getConfig().getString("ranks." + rank + ".prefix");
        return "";
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

    public static Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return null;
    }
}
