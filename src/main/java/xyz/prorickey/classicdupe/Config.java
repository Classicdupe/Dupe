package xyz.prorickey.classicdupe;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;
import xyz.prorickey.classicdupe.commands.perk.SuffixCMD;
import xyz.prorickey.classicdupe.events.BlockPlace;

public class Config {

    private static FileConfiguration config;
    private static File configFile;

    public static void init(JavaPlugin p) {
        configFile = new File(p.getDataFolder() + "/config.yml");
        if(!configFile.exists()) {
            p.saveResource("config.yml", false);
        }
        reloadConfig();
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        Config.getConfig().getStringList("blockFromPlacing").forEach(str -> BlockPlace.bannedToPlaceBcAnnoyingASF.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("forbiddenDupes").forEach(str -> DupeCMD.forbiddenDupes.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("forbiddenDupesInCombat").forEach(str -> DupeCMD.forbiddenDupesInCombat.add(Material.valueOf(str.toUpperCase())));
        Config.getConfig().getStringList("removedItems").forEach(str -> ClassicDupe.randomItems.remove(new ItemStack(Material.valueOf(str.toUpperCase()))));
        MemorySection sec = (MemorySection) Config.getConfig().get("suffix");
        assert sec != null;
        sec.getKeys(true).forEach((name) -> SuffixCMD.suffixes.put(name, Config.getConfig().getString("suffix." + name)));
    }
    public static FileConfiguration getConfig() { return config; }

}