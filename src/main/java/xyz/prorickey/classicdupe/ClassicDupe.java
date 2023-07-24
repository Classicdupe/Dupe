package xyz.prorickey.classicdupe;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.event.ClickEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.luckperms.api.LuckPerms;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.Clans;
import xyz.prorickey.classicdupe.clans.subcommands.CSChat;
import xyz.prorickey.classicdupe.commands.admin.*;
import xyz.prorickey.classicdupe.commands.default1.*;
import xyz.prorickey.classicdupe.commands.moderator.*;
import xyz.prorickey.classicdupe.commands.perk.*;
import xyz.prorickey.classicdupe.custom.CustomSets;
import xyz.prorickey.classicdupe.customitems.cicommands;
import xyz.prorickey.classicdupe.database.Database;
import xyz.prorickey.classicdupe.database.PlayerVaultDatabase;
import xyz.prorickey.classicdupe.discord.BoosterService;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;
import xyz.prorickey.classicdupe.events.*;
import xyz.prorickey.classicdupe.metrics.Metrics;
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;
import xyz.prorickey.classicdupe.playerevents.MAZEmanager;
import xyz.prorickey.classicdupe.playerevents.maze.PlayerOnEnd;
import xyz.prorickey.classicdupe.playerevents.maze.commands.MazeCommands;

public class ClassicDupe extends JavaPlugin {

    public static JavaPlugin plugin;
    public static ClassicDupeBot bot;
    public static LuckPerms lpapi;
    public static Economy econ;
    public static Database database;
    public static ClanDatabase clanDatabase;
    public static PlayerVaultDatabase pvdatabase;

    public static final List<ItemStack> randomItems = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;

        try {
            Class.forName("org.h2.Driver");
            Class.forName ("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Metrics.init(this);
        RegisteredServiceProvider<LuckPerms> lppro = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(lppro != null) { lpapi = lppro.getProvider(); }
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new ClassicDupeExpansion(this).register();
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp != null) { econ = rsp.getProvider(); }

        Config.init(this);
        database = new Database();
        pvdatabase = new PlayerVaultDatabase(this);

        Bukkit.getServicesManager().register(
                Economy.class,
                new VaultEconomy(database.getPlayerDatabase()),
                Bukkit.getPluginManager().getPlugin("Vault"),
                ServicePriority.Normal
        );

        bot = new ClassicDupeBot(this);

        new JoinEvent.JoinEventTasks().runTaskTimer(this, 0, 20);
        new TpaCMD.TPATask().runTaskTimer(this, 0, 20);
        new Combat.CombatTask().runTaskTimer(this, 0, 10);
        new LeaderBoardTask().runTaskTimer(this, 0, 20*60);
        new BroadcastTask().runTaskTimer(this, 0, 20*90);
        new LinkCMD.LinkCodeTask().runTaskTimer(this, 0, 20);
        new Scoreboard.ScoreboardTask().runTaskTimer(this, 0, 10);
        new ClearSpawn.ClearSpawnTask().runTaskTimer(this, 0, 20);
        new ArmorTrims.ArmorTrimsTask().runTaskTimer(this, 0, 20);

        new Clans(this);
        KOTHEventManager.init(this);
        CustomSets.init();

        for (Material value : Material.values()) randomItems.add(new ItemStack(value));
        for (Enchantment value : Enchantment.values()) {
            for(int i = 1; i < value.getMaxLevel(); i++) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                meta.addStoredEnchant(value, i, false);
                book.setItemMeta(meta);
                randomItems.add(book);
            }
        }

        ShopCMD.reloadShop();
        this.getCommand("dupe").setExecutor(new DupeCMD());
        this.getCommand("dupe").setTabCompleter(new DupeCMD());
        this.getCommand("filter").setExecutor(new FilterCMD());
        this.getCommand("filter").setTabCompleter(new FilterCMD());
        this.getCommand("random").setExecutor(new RandomCMD());
        this.getCommand("random").setTabCompleter(new RandomCMD());
        this.getCommand("spawn").setExecutor(new SpawnCMD());
        this.getCommand("spawn").setTabCompleter(new SpawnCMD());
        this.getCommand("nether").setExecutor(new SpawnCMD());
        this.getCommand("nether").setTabCompleter(new SpawnCMD());
        this.getCommand("overworld").setExecutor(new SpawnCMD());
        this.getCommand("overworld").setTabCompleter(new SpawnCMD());
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
        this.getCommand("maze").setExecutor(new MazeCommands());
        this.getCommand("joinmaze").setExecutor(new MazeCommands());
        PluginCommand scCmd = plugin.getServer().getPluginCommand("sc");
        if(scCmd.getPlugin().getPluginMeta().getName().equals("OpenInv")) {
            scCmd.setExecutor(new StaffChatCMD());
            scCmd.setTabCompleter(new StaffChatCMD());
        } else {
            this.getCommand("sc").setExecutor(new StaffChatCMD());
            this.getCommand("sc").setTabCompleter(new StaffChatCMD());
        }
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
        this.getCommand("cspy").setExecutor(new CspyCMD());
        this.getCommand("cspy").setTabCompleter(new CspyCMD());
        PluginCommand discordCmd = plugin.getServer().getPluginCommand("discord");
        if(discordCmd.getPlugin().getPluginMeta().getName().equals("DiscordSRV")) {
            discordCmd.setExecutor(new DiscordCMD());
            discordCmd.setTabCompleter(new DiscordCMD());
        } else {
            this.getCommand("discord").setExecutor(new DiscordCMD());
            this.getCommand("discord").setTabCompleter(new DiscordCMD());
        }
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
        this.getCommand("feed").setExecutor(new FeedCMD());
        this.getCommand("feed").setTabCompleter(new FeedCMD());
        this.getCommand("configreload").setExecutor(new ConfigReload());
        this.getCommand("configreload").setTabCompleter(new ConfigReload());
        this.getCommand("hat").setExecutor(new HatCMD());
        this.getCommand("hat").setTabCompleter(new HatCMD());
        this.getCommand("report").setExecutor(new ReportCMD());
        this.getCommand("report").setTabCompleter(new ReportCMD());
        this.getCommand("craft").setExecutor(new CraftCMD());
        this.getCommand("craft").setTabCompleter(new CraftCMD());
        this.getCommand("staffteleport").setExecutor(new StaffTeleportCMD());
        this.getCommand("staffteleport").setTabCompleter(new StaffTeleportCMD());
        this.getCommand("back").setExecutor(new BackCMD());
        this.getCommand("back").setExecutor(new BackCMD());
        //this.getCommand("blockcommands").setExecutor(new BlockCommandCMD());
        //this.getCommand("blockcommands").setTabCompleter(new BlockCommandCMD());
        this.getCommand("night").setExecutor(new NightVisionCMD());
        this.getCommand("night").setTabCompleter(new NightVisionCMD());
        this.getCommand("home").setExecutor(new HomeCMD());
        this.getCommand("home").setTabCompleter(new HomeCMD());
        this.getCommand("sethome").setExecutor(new SetHomeCMD());
        this.getCommand("sethome").setTabCompleter(new SetHomeCMD());
        this.getCommand("delhome").setExecutor(new DelHomeCMD());
        this.getCommand("delhome").setTabCompleter(new DelHomeCMD());
        this.getCommand("clanchat").setExecutor(new CSChat());
        this.getCommand("clanchat").setTabCompleter(new CSChat());
        this.getCommand("shop").setExecutor(new ShopCMD());
        this.getCommand("shop").setTabCompleter(new ShopCMD());
        this.getCommand("balance").setExecutor(new BalanceCMD());
        this.getCommand("balance").setTabCompleter(new BalanceCMD());
        this.getCommand("customitem").setExecutor(new cicommands());
        this.getCommand("customitem").setTabCompleter(new cicommands());
        this.getCommand("bounty").setExecutor(new BountyCMD());
        this.getCommand("bounty").setTabCompleter(new BountyCMD());
        this.getCommand("deathmessages").setExecutor(new DeathMessagesCMD());
        this.getCommand("deathmessages").setTabCompleter(new DeathMessagesCMD());
        this.getCommand("mutepings").setExecutor(new MutePingsCMD());
        this.getCommand("mutepings").setTabCompleter(new MutePingsCMD());
        headlines hl = new headlines();
        this.getCommand("headline").setExecutor(hl);
        this.getCommand("alert").setExecutor(hl);
        this.getCommand("subhead").setExecutor(hl);
        this.getCommand("headline").setTabCompleter(hl);
        this.getCommand("alert").setTabCompleter(hl);
        this.getCommand("subhead").setTabCompleter(hl);
        this.getCommand("help").setExecutor(new HelpCMD());
        this.getCommand("help").setTabCompleter(new HelpCMD());
        this.getCommand("customset").setExecutor(new CustomSetCMD());
        this.getCommand("customset").setTabCompleter(new CustomSetCMD());
        this.getCommand("afk").setExecutor(new SpawnCMD());
        this.getCommand("afk").setTabCompleter(new SpawnCMD());
        this.getCommand("worldsize").setExecutor(new WorldsizeCMD());
        this.getCommand("worldsize").setTabCompleter(new WorldsizeCMD());
        this.getCommand("pay").setExecutor(new PayCMD());
        this.getCommand("pay").setTabCompleter(new PayCMD());

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
        getServer().getPluginManager().registerEvents(new ClearSpawn(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldown(), this);
        getServer().getPluginManager().registerEvents(new CSPY(), this);
        getServer().getPluginManager().registerEvents(new ReducedFireworkLag(), this);
        //getServer().getPluginManager().registerEvents(new CommandSendEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerOnEnd(), this);
        getServer().getPluginManager().registerEvents(new ShopCMD(), this);
        getServer().getPluginManager().registerEvents(new ArmorTrims(), this);
        getServer().getPluginManager().registerEvents(new ItemUse(), this);
        //getServer().getPluginManager().registerEvents(new CraftingTable(), this);
        getServer().getPluginManager().registerEvents(new BountyCMD(), this);
        getServer().getPluginManager().registerEvents(new FixArmorTrimCrafting(), this);
        getServer().getPluginManager().registerEvents(new HelpCMD(), this);
        getServer().getPluginManager().registerEvents(new BoosterService(), this);

        getServer().getPluginManager().registerEvents(new PvSeeCMD(), this);
        //set command & tab completer
        this.getCommand("pvsee").setExecutor(new PvSeeCMD());
        this.getCommand("pvsee").setTabCompleter(new PvSeeCMD());
        //Init maze
        MAZEmanager.init();

        //ticker

    }

    @Override
    public void onDisable() {
        ClassicDupeBot.jda.shutdown();
    }

    public static void executeConsoleCommand(String cmd) {
        ClassicDupe.getPlugin().getServer().dispatchCommand(ClassicDupe.getPlugin().getServer().getConsoleSender(), cmd);
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
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                    player.sendMessage(Utils.format("<aqua><bold>STORE</bold> <dark_gray>| <green>Check out our store at ")
                            .append(Utils.format("<yellow>https://classicdupe.tebex.io")
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://classicdupe.tebex.io")))
                            .append(Utils.format(" <green>or by executing "))
                            .append(Utils.format("<yellow>/buy")
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/buy"))));
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                });
            } else {
                lastBroadcast = LastBroadcast.DISCORD;
                ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                    player.sendMessage(Utils.format("<aqua><bold>DISCORD</bold> <dark_gray>| <green>Check out our discord at ")
                            .append(Utils.format("<yellow>https://discord.gg/FZtcF3pBu6")
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/FZtcF3pBu6")))
                            .append(Utils.format(" <green>or by executing "))
                            .append(Utils.format("<yellow>/discord")
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/discord"))));
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
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
    public static ClanDatabase getClanDatabase() { return clanDatabase; }

    public static List<String> getOnlinePlayerUsernames() {
        List<String> list = new ArrayList<>();
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if(!Utils.isVanished(player)) list.add(player.getName());
        });
        return list;
    }

    public static List<String> getOnlinePlayerUsernames(Player player) {
        List<String> list = new ArrayList<>();
        if(player.hasPermission("mod.seevanish")) plugin.getServer().getOnlinePlayers().forEach(playe -> {
            list.add(playe.getName());
        });
        else plugin.getServer().getOnlinePlayers().forEach(playe -> {
            if(!Utils.isVanished(playe)) list.add(playe.getName());
        });
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
        rawBroadcast("<red><b>The server will restart in 60 seconds.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("<red><b>The server will restart in 30 seconds.");
        }, 20L * 30L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("<red><b>The server will restart in 10 seconds.");
        }, 20L * 50L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("<red><b>The server will restart in 5 seconds.");
        }, 20L * 55L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (scheduledRestartCanceled) return;
            restartInProgress = false;
            plugin.getServer().shutdown();
        }, 20L * 60L);
    }

}
