package xyz.prorickey.classicdupe;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.luckperms.api.LuckPerms;
import xyz.prorickey.classicdupe.commands.admin.*;
import xyz.prorickey.classicdupe.commands.default1.*;
import xyz.prorickey.classicdupe.commands.moderator.*;
import xyz.prorickey.classicdupe.commands.perk.*;
import xyz.prorickey.classicdupe.database.Database;
import xyz.prorickey.classicdupe.database.PlayerVaultDatabase;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;
import xyz.prorickey.classicdupe.events.*;

public class ClassicDupe extends JavaPlugin {

    public static JavaPlugin plugin;
    public static ClassicDupeBot bot;
    public static LuckPerms lpapi;
    public static Database database;
    public static PlayerVaultDatabase pvdatabase;

    @Override
    public void onEnable() {
        plugin = this;
        Config.init(this);
        database = new Database();
        pvdatabase = new PlayerVaultDatabase(this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) { lpapi = provider.getProvider(); }
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new ClassicDupeExpansion(this).register();

        bot = new ClassicDupeBot(this);

        new JoinEvent.JoinEventTasks().runTaskTimer(ClassicDupe.getPlugin(), 0, 20);
        new TpaCMD.TPATask().runTaskTimer(ClassicDupe.getPlugin(), 0, 20);
        new Combat.CombatTask().runTaskTimer(ClassicDupe.getPlugin(), 0, 10);
        new LeaderBoardTask().runTaskTimer(ClassicDupe.getPlugin(), 0, 20*60);
        new BroadcastTask().runTaskTimer(ClassicDupe.getPlugin(), 0, 20*90);
        new LinkCMD.LinkCodeTask().runTaskTimer(ClassicDupe.getPlugin(), 0, 20);

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
        this.getCommand("chatcolor").setExecutor(new ChatColorCMD());
        this.getCommand("chatcolor").setTabCompleter(new ChatColorCMD());
        this.getCommand("gradient").setExecutor(new ChatGradientCMD());
        this.getCommand("gradient").setTabCompleter(new ChatGradientCMD());
        this.getCommand("staffchat").setExecutor(new StaffChatCMD());
        this.getCommand("staffchat").setTabCompleter(new StaffChatCMD());
        this.getCommand("repair").setExecutor(new RepairCMD());
        this.getCommand("clearchat").setExecutor(new ClearChatCMD());
        this.getCommand("pm").setExecutor(new PrivateMessageCMD());
        this.getCommand("pm").setTabCompleter(new PrivateMessageCMD());
        this.getCommand("pmr").setExecutor(new PrivateMessageReplyCMD());
        this.getCommand("pmr").setTabCompleter(new PrivateMessageReplyCMD());
        this.getCommand("head").setExecutor(new HeadCMD());
        this.getCommand("head").setTabCompleter(new HeadCMD());
        this.getCommand("spec").setExecutor(new SpecCMD());
        this.getCommand("spec").setTabCompleter(new SpecCMD());
        this.getCommand("broadcast").setExecutor(new BroadcastCMD());
        this.getCommand("broadcast").setTabCompleter(new BroadcastCMD());
        this.getCommand("sudo").setExecutor(new SudoCMD());
        this.getCommand("sudo").setTabCompleter(new SudoCMD());
        this.getCommand("pv").setExecutor(new PlayerVaultCMD());
        this.getCommand("pv").setTabCompleter(new PlayerVaultCMD());
        this.getCommand("pvadd").setExecutor(new PvAddCMD());
        this.getCommand("pvadd").setTabCompleter(new PvAddCMD());
        this.getCommand("invsee").setExecutor(new InvseeCMD());
        this.getCommand("invsee").setTabCompleter(new InvseeCMD());
        this.getCommand("trash").setExecutor(new TrashCMD());
        this.getCommand("trash").setTabCompleter(new TrashCMD());
        this.getCommand("suffix").setExecutor(new SuffixCMD());
        this.getCommand("suffix").setTabCompleter(new SuffixCMD());
        this.getCommand("tpa").setExecutor(new TpaCMD());
        this.getCommand("tpa").setTabCompleter(new TpaCMD());
        this.getCommand("tpaccept").setExecutor(new TpacceptCMD());
        this.getCommand("tpaccept").setTabCompleter(new TpacceptCMD());
        this.getCommand("tpacancel").setExecutor(new TpacancelCMD());
        this.getCommand("tpacancel").setTabCompleter(new TpacancelCMD());
        this.getCommand("tpadecline").setExecutor(new TpadeclineCMD());
        this.getCommand("tpadecline").setTabCompleter(new TpadeclineCMD());
        this.getCommand("stats").setExecutor(new StatsCMD());
        this.getCommand("stats").setTabCompleter(new StatsCMD());
        this.getCommand("discord").setExecutor(new DiscordCMD());
        this.getCommand("discord").setTabCompleter(new DiscordCMD());
        this.getCommand("enderchest").setExecutor(new EnderChestCMD());
        this.getCommand("enderchest").setTabCompleter(new EnderChestCMD());
        this.getCommand("rename").setExecutor(new RenameCMD());
        this.getCommand("rename").setTabCompleter(new RenameCMD());
        this.getCommand("nickname").setExecutor(new NicknameCMD());
        this.getCommand("nickname").setTabCompleter(new NicknameCMD());
        this.getCommand("rules").setExecutor(new RulesCMD());
        this.getCommand("rules").setTabCompleter(new RulesCMD());
        this.getCommand("nakedoff").setExecutor(new NakedOffCMD());
        this.getCommand("nakedoff").setTabCompleter(new NakedOffCMD());
        this.getCommand("link").setExecutor(new LinkCMD());
        this.getCommand("link").setTabCompleter(new LinkCMD());
        this.getCommand("unlink").setExecutor(new UnlinkCMD());
        this.getCommand("unlink").setTabCompleter(new UnlinkCMD());

        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new QuitEvent(), this);
        getServer().getPluginManager().registerEvents(new CancelPortalCreation(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        getServer().getPluginManager().registerEvents(new VoidTeleport(), this);
        getServer().getPluginManager().registerEvents(new ChatColorCMD(), this);
        getServer().getPluginManager().registerEvents(new ChatGradientCMD(), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerVaultCMD(), this);
        getServer().getPluginManager().registerEvents(new SuffixCMD(), this);
        getServer().getPluginManager().registerEvents(new EntitySpawnEvent(), this);
        getServer().getPluginManager().registerEvents(new Combat(), this);
        getServer().getPluginManager().registerEvents(new FlowEvent(), this);
        getServer().getPluginManager().registerEvents(new GoldenAppleCooldown(), this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, ClassicDupe::scheduleRestart, 20L * 60L * 60L * 24L);
    }

    @Override
    public void onDisable() {
        bot.jda.shutdown();
    }

    private enum LastBroadcast {
        DISCORD,
        STORE
    }

    private static LastBroadcast lastBroadcast;

    private static class BroadcastTask extends BukkitRunnable {
        @Override
        public void run() {
            if(lastBroadcast != null && lastBroadcast.equals(LastBroadcast.DISCORD)) {
                lastBroadcast = LastBroadcast.STORE;
                ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Component.text(Utils.format("")));
                    player.sendMessage(Utils.format("&a-----------------------------------------------------"));
                    player.sendMessage(Component.text(Utils.format("&b&lSTORE &8| &aCheck out our store at "))
                            .append(Component.text(Utils.format("&ehttps://classicdupe.tebex.io"))
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://classicdupe.tebex.io")))
                            .append(Component.text(Utils.format(" &aor by executing ")))
                            .append(Component.text(Utils.format("&e/buy"))
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/buy"))));
                    player.sendMessage(Utils.format("&a-----------------------------------------------------"));
                });
            } else {
                lastBroadcast = LastBroadcast.DISCORD;
                ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Component.text(Utils.format("")));
                    player.sendMessage(Utils.format("&a-----------------------------------------------------"));
                    player.sendMessage(Component.text(Utils.format("&b&lDISCORD &8| &aCheck out our discord at "))
                            .append(Component.text(Utils.format("&ehttps://discord.gg/FZtcF3pBu6"))
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/FZtcF3pBu6")))
                            .append(Component.text(Utils.format(" &aor by executing ")))
                            .append(Component.text(Utils.format("&e/discord"))
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/discord"))));
                    player.sendMessage(Utils.format("&a-----------------------------------------------------"));
                });
            }
        }
    }

    private static class LeaderBoardTask extends BukkitRunnable {
        @Override
        public void run() {
            database.getPlayerDatabase().reloadLeaderboards();
        }
    }

    public static JavaPlugin getPlugin() { return plugin; }
    public static LuckPerms getLPAPI() { return lpapi; }
    public static Database getDatabase() { return database; }
    public static PlayerVaultDatabase getPVDatabase() { return pvdatabase; }

    public static List<String> getOnlinePlayerUsernames() {
        List<String> list = new ArrayList<>();
        plugin.getServer().getOnlinePlayers().forEach(player -> list.add(player.getName()));
        return list;
    }

    public static void rawBroadcast(String text) {
        plugin.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(Utils.format(text)));
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
