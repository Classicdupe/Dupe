package xyz.prorickey.classicdupe;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private static FileConfiguration config;
    private static File configFile;

    public static FileConfiguration init(JavaPlugin p) {
        configFile = new File(p.getDataFolder() + "/config.yml");
        if(!configFile.exists()) {
            p.saveResource("config.yml", false);
        }
        return reloadConfig();
    }

    public static void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        return config;
    }

    public static FileConfiguration getConfig() { return config; }

}