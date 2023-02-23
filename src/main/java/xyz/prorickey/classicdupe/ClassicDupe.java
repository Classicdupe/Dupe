package xyz.prorickey.classicdupe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import xyz.prorickey.classicdupe.commands.admin.FilterCMD;
import xyz.prorickey.classicdupe.commands.admin.GamemodeCMD;
import xyz.prorickey.classicdupe.commands.admin.GmaCMD;
import xyz.prorickey.classicdupe.commands.admin.GmcCMD;
import xyz.prorickey.classicdupe.commands.admin.GmsCMD;
import xyz.prorickey.classicdupe.commands.admin.GmspCMD;
import xyz.prorickey.classicdupe.commands.admin.SetSpawnCMD;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;
import xyz.prorickey.classicdupe.commands.default1.RandomCMD;
import xyz.prorickey.classicdupe.commands.default1.SpawnCMD;
import xyz.prorickey.classicdupe.database.Database;
import xyz.prorickey.classicdupe.events.BlockPlace;
import xyz.prorickey.classicdupe.events.CancelPortalCreation;
import xyz.prorickey.classicdupe.events.Chat;
import xyz.prorickey.classicdupe.events.JoinEvent;
import xyz.prorickey.classicdupe.events.QuitEvent;
import xyz.prorickey.classicdupe.events.VoidTeleport;

public class ClassicDupe extends JavaPlugin {

    public static JavaPlugin plugin;
    public static LuckPerms lpapi;
    public static Database database;

    @Override
    public void onEnable() {
        plugin = this;
        Config.init(this);
        database = new Database();

        Config.getConfig().getStringList("blockFromPlacing").forEach(str -> {
            BlockPlace.bannedToPlaceBcAnnoyingASF.add(Material.valueOf(str));
        });

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lpapi = provider.getProvider();
        }

        enableNightVision();

        this.getCommand("dupe").setExecutor(new DupeCMD());
        this.getCommand("dupe").setTabCompleter(new DupeCMD());
        this.getCommand("filter").setExecutor(new FilterCMD());
        this.getCommand("filter").setTabCompleter(new FilterCMD());
        this.getCommand("random").setExecutor(new RandomCMD());
        this.getCommand("random").setTabCompleter(new RandomCMD());
        this.getCommand("spawn").setExecutor(new SpawnCMD());
        this.getCommand("spawn").setTabCompleter(new SpawnCMD());
        this.getCommand("setspawn").setExecutor(new SetSpawnCMD());
        this.getCommand("setspawn").setTabCompleter(new SetSpawnCMD());
        this.getCommand("gamemode").setExecutor(new GamemodeCMD());
        this.getCommand("gamemode").setTabCompleter(new GamemodeCMD());
        this.getCommand("gmc").setExecutor(new GmcCMD());
        this.getCommand("gmc").setTabCompleter(new GmcCMD());
        this.getCommand("gma").setExecutor(new GmaCMD());
        this.getCommand("gma").setTabCompleter(new GmaCMD());
        this.getCommand("gms").setExecutor(new GmsCMD());
        this.getCommand("gms").setTabCompleter(new GmsCMD());
        this.getCommand("gmsp").setExecutor(new GmspCMD());
        this.getCommand("gmsp").setTabCompleter(new GmspCMD());

        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new QuitEvent(), this);
        getServer().getPluginManager().registerEvents(new CancelPortalCreation(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        getServer().getPluginManager().registerEvents(new VoidTeleport(), this);
    }

    @Override
    public void onDisable() {
        database.shutdown();
    }

    private static void enableNightVision() {
        NightVisionTask task = new NightVisionTask();
        task.runTaskTimer(ClassicDupe.getPlugin(), 0, 20*5);
    }

    private static class NightVisionTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.NIGHT_VISION,
                        999999999,
                        1
                ));
            });
        }
    }

    public static JavaPlugin getPlugin() { return plugin; }
    public static LuckPerms getLPAPI() { return lpapi; }
    public static Database getDatabase() { return database; }

    public static List<String> getOnlinePlayerUsernames() {
        List<String> list = new ArrayList<>();
        plugin.getServer().getOnlinePlayers().forEach(player -> list.add(player.getName()));
        return list;
    }

    public static void rawBroadcast(String text) {
        MiniMessage mm = MiniMessage.miniMessage();
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.sendMessage(mm.deserialize(Utils.format(text)));
        });
    }

    public static Boolean scheduledRestartCanceled = false;

    public static void scheduleRestart() {
        scheduledRestartCanceled = false;
        rawBroadcast("&c&lThe server will restart in 60 seconds.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(!scheduledRestartCanceled) plugin.getServer().shutdown();
            }
        }, 1200L);
    }

}
