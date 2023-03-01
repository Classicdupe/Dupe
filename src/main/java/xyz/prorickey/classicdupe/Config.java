package xyz.prorickey.classicdupe;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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

    public static void reloadConfig() { config = YamlConfiguration.loadConfiguration(configFile);}
    public static FileConfiguration getConfig() { return config; }

}