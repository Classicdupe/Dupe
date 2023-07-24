package xyz.prorickey.classicdupe.playerevents;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.koth.KRunnable;
import xyz.prorickey.classicdupe.playerevents.koth.commands.KothKCMD;
import xyz.prorickey.classicdupe.playerevents.koth.events.KEPoints;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KOTHEventManager {

    private static JavaPlugin plugin;
    private static long timeSinceLastKOTH = 0;
    private static boolean automatic = false;

    public static boolean running = false;
    public static Map<UUID, PlayerKothData> kothData = new HashMap<>();
    public static ProtectedCuboidRegion region = null;
    private static BukkitTask carpetTask = null;

    public static void init(JavaPlugin pl) {
        plugin = pl;

        pl.getCommand("koth").setExecutor(new KothKCMD());
        pl.getCommand("koth").setTabCompleter(new KothKCMD());

        pl.getServer().getPluginManager().registerEvents(new KEPoints(), pl);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ClassicDupe.getPlugin(), new KothManagerRunnable(), 0, 20*5);
    }

    public static class KothManagerRunnable implements Runnable {
        @Override
        public void run() {
            if(timeSinceLastKOTH == 0) timeSinceLastKOTH = System.currentTimeMillis();
            else if(timeSinceLastKOTH + (1000*60*60*6) < System.currentTimeMillis()) {
                startKOTH();
                automatic = true;
            }
        }
    }

    /* Koth Perms

        koth.verbose

        39 64 39
        -39 110 -39

    */

    public static PlayerKothData getPlayerKothData(Player player) {
        if(kothData.containsKey(player.getUniqueId())) return kothData.get(player.getUniqueId());
        else {
            PlayerKothData data = new PlayerKothData(player);
            kothData.put(player.getUniqueId(), data);
            return data;
        }
    }

    // TODO: Load players when they join too

    public static void startKOTH() {
        String KOTHSchem = getRandomKOTHSchem();
        try {
            loadSchem(KOTHSchem);
        } catch (IOException | WorldEditException e) {
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("koth.verbose")) p.sendMessage(Utils.cmdMsg(
                        "<red>KOTH Attempted to load schematic but failed. Canceling event."
                ));
            });
            return;
        }
        region = new ProtectedCuboidRegion("tempKothEvent",
                true,
                BlockVector3.at(39, 64, 39),
                BlockVector3.at(-39, 110, -39)
        );
        region.setFlag(Flags.FALL_DAMAGE, StateFlag.State.ALLOW);
        region.setPriority(10);
        WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(Bukkit.getWorld("world"))).addRegion(region);
        carpetTask = new KRunnable().runTaskTimer(plugin, 0, 1);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(Utils.format("<gold><bold>KOTH</bold> <gray>| <yellow>Koth event has started")));
        running = true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(ClassicDupe.getPlugin(), KOTHEventManager::endKOTHEvent, 20*60*30);
    }

    // TODO: Portals don't work for regular players

    public static void endKOTHEvent() {
        if(!running) return;
        timeSinceLastKOTH = System.currentTimeMillis();
        carpetTask.cancel();
        KRunnable.bossBars.forEach(Audience::hideBossBar);
        KRunnable.bossBars.clear();
        WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(Bukkit.getWorld("world"))).removeRegion(region.getId());
        CuboidRegion rg = new CuboidRegion(BukkitAdapter.adapt(Bukkit.getWorld("world")), region.getMinimumPoint(), region.getMaximumPoint());
        rg.forEach(bv3 -> {
            if(bv3.getY() == 64) Bukkit.getWorld("world").getBlockAt(bv3.getX(), bv3.getY(), bv3.getZ()).setType(Material.BEDROCK);
            else Bukkit.getWorld("world").getBlockAt(bv3.getX(), bv3.getY(), bv3.getZ()).setType(Material.AIR);
        });
        region = null;
        running = false;
        Map<PlayerKothData, Integer> kothPoints = new HashMap<>();
        Map<PlayerKothData, Integer> kothKills = new HashMap<>();
        kothData.forEach((uuid, data) -> {
            kothPoints.put(data, data.getPoints());
            kothKills.put(data, data.getKills());
        });
        Map<Integer, PlayerKothData> topPoints =
                kothPoints.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(3)
                        .collect(Collectors.toMap(
                                Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
        Map<Integer, PlayerKothData> topKills =
                kothKills.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(3)
                        .collect(Collectors.toMap(
                                Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
        List<PlayerKothData> topPointsList = topPoints.values().stream().toList();
        List<PlayerKothData> topKillsList = topKills.values().stream().toList();
        List<Component> topPointsComp = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            if(topPointsList.size() <= i) break;
            topPointsComp.add(Utils.format("<yellow>" + i+1 + ". " + topPointsList.get(i).getOffPlayer().getName() + " - " + topPointsList.get(i).getPoints())
                    .hoverEvent(HoverEvent.showText(
                            Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topPointsList.get(i).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topPointsList.get(i).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topPointsList.get(i).getPoints()))
                    ));
            if(automatic) ClassicDupe.getDatabase().getPlayerDatabase()
                    .getPlayerData(topPointsList.get(i).getOffPlayer().getUniqueId())
                    .addBalance(300-(i*100));
        }
        List<Component> topKillsComp = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            if(topKillsList.size() <= i) break;
            topKillsComp.add(Utils.format("<yellow>" + i+1 + ". " + topKillsList.get(i).getOffPlayer().getName() + " - " + topKillsList.get(i).getKills())
                    .hoverEvent(HoverEvent.showText(
                            Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topKillsList.get(i).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topKillsList.get(i).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topKillsList.get(i).getPoints()))
                    ));
            if(automatic) ClassicDupe.getDatabase().getPlayerDatabase()
                    .getPlayerData(topKillsList.get(i).getOffPlayer().getUniqueId())
                    .addBalance(300-(i*100));
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Utils.format("<yellow><b>The KOTH event has ended"));
            if(automatic) p.sendMessage(Utils.format("<yellow>You will be rewarded in dabloons for your placement"));
            p.sendMessage(Utils.format("<yellow>--- <gold>Top Points <yellow>---"));
            topKillsComp.forEach(p::sendMessage);
            p.sendMessage(Utils.format(" "));
            p.sendMessage(Utils.format("<yellow>--- <gold>Top Kills <yellow>---"));
            topPointsComp.forEach(p::sendMessage);
        });
        kothData = new HashMap<>();
        automatic = false;
    }

    public static String getRandomKOTHSchem() {
        int randomIndex = new Random().nextInt(Config.getConfig().getStringList("events.koth.schems").size());
        return Config.getConfig().getStringList("events.koth.schems").get(randomIndex);
    }

    public static void loadSchem(String name) throws IOException, WorldEditException {
        File schemFile = new File(plugin.getDataFolder().getAbsolutePath() + "/schems/" + name + ".schem");
        Clipboard clipboard = ClipboardFormats.findByFile(schemFile).getReader(new FileInputStream(schemFile)).read();
        EditSession ses = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")));
        Operation op = new ClipboardHolder(clipboard)
                .createPaste(ses)
                .to(BlockVector3.at(0, 65, 0))
                .ignoreAirBlocks(true)
                .build();
        Operations.complete(op);
        ses.close();
    }

    public static class PlayerKothData {
        private final OfflinePlayer offPlayer;
        private int kills = 0;
        private int deaths = 0;
        private int points = 0;
        public PlayerKothData(Player player) { this.offPlayer = player; }
        public OfflinePlayer getOffPlayer() { return offPlayer; }
        public int getKills() { return kills; }
        public int getDeaths() { return deaths; }
        public int getPoints() { return points; }
        public void addKill() { kills+=1; }
        public void addDeath() { deaths+=1; }
        public void addPoints(int num) { points+=num; }
    }

}
