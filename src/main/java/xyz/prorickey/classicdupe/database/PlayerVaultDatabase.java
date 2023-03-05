package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerVaultDatabase {

    private final File dir;

    public PlayerVaultDatabase(JavaPlugin plugin) {
        dir = new File(plugin.getDataFolder() + "/pvs/");
        if(!dir.exists()) dir.mkdir();
    }

    public void setItemInVault(String uuid, int vault, int pos, ItemStack item) {
        File playerFile = new File(dir + "/" + uuid + ".yml");
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        yaml.set("pvs." + vault + "." + pos, item);
        try {
            yaml.save(playerFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    @Nullable
    public Map<Integer, ItemStack> getVault(String uuid, int num) {
        File playerFile = new File(dir + "/" + uuid + ".yml");
        if(!playerFile.exists()) return null;
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        if(yaml.get("pvs." + num) == null) return null;
        Map<Integer, ItemStack> itemsMap = new HashMap<>();
        for(int i = 0; i < 54; i++) itemsMap.put(i, (ItemStack) yaml.get("pvs." + num + "." + i));
        return itemsMap;
    }

    public void setVault(String uuid, int num, Inventory inv) {
        File playerFile = new File(dir + "/" + uuid + ".yml");
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        for(int i = 0; i < 54; i++) yaml.set("pvs." + num + "." + i, inv.getItem(i) == null ? new ItemStack(Material.AIR) : inv.getItem(i));
        try {
            yaml.save(playerFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void addVault(String uuid) {
        File playerFile = new File(dir + "/" + uuid + ".yml");
        if(!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        if(yaml.get("pvnum") == null) {
            yaml.set("pvnum", 1);
            for(int i = 0; i < 54; i++) yaml.set("pvs.1." + i, new ItemStack(Material.AIR));
        }
        else {
            yaml.set("pvnum", yaml.getInt("pvnum") + 1);
            for(int i = 0; i < 54; i++) yaml.set("pvs." + yaml.getInt("pvnum") + "." + i, new ItemStack(Material.AIR));
        }
        try {
            yaml.save(playerFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

}
