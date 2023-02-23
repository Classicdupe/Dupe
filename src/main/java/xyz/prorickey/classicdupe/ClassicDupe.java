package xyz.prorickey.classicdupe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.luckperms.api.LuckPerms;
import xyz.prorickey.classicdupe.commands.admin.*;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;
import xyz.prorickey.classicdupe.commands.default1.RandomCMD;
import xyz.prorickey.classicdupe.commands.default1.SpawnCMD;
import xyz.prorickey.classicdupe.commands.moderator.MutechatCMD;
import xyz.prorickey.classicdupe.database.Database;
import xyz.prorickey.classicdupe.events.*;

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
            BlockPlace.bannedToPlaceBcAnnoyingASF.add(Material.valueOf(str.toUpperCase()));
        });

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) { lpapi = provider.getProvider(); }

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
        this.getCommand("schedulerestart").setExecutor(new ScheduleRestartCMD());
        this.getCommand("schedulerestart").setTabCompleter(new ScheduleRestartCMD());
        this.getCommand("mutechat").setExecutor(new MutechatCMD());
        this.getCommand("mutechat").setTabCompleter(new MutechatCMD());

        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new QuitEvent(), this);
        getServer().getPluginManager().registerEvents(new CancelPortalCreation(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        getServer().getPluginManager().registerEvents(new VoidTeleport(), this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            scheduleRestart();
        }, 20L * 60L * 60L * 24L);
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
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.sendMessage(Utils.format(text));
        });
    }

    public static Boolean scheduledRestartCanceled = false;
    public static Boolean restartInProgress = false;

    public static void scheduleRestart() {
        scheduledRestartCanceled = false;
        restartInProgress = true;
        rawBroadcast("&c&lThe server will restart in 60 seconds.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("&c&lThe server will restart in 30 seconds.");
        }, 20L * 30L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("&c&lThe server will restart in 10 seconds.");
        }, 20L * 50L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("&c&lThe server will restart in 5 seconds.");
        }, 20L * 55L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            restartInProgress = false;
            plugin.getServer().shutdown();
        }, 20L * 60L);
    }

}
