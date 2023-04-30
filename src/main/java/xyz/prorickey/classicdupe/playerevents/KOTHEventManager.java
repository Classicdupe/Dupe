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
    private static long timeSinceLastKOTH;

    public static boolean running = false;
    public static Map<UUID, PlayerKothData> kothData = new HashMap<>();
    public static ProtectedCuboidRegion region = null;
    private static BukkitTask carpetTask = null;

    public static void init(JavaPlugin pl) {
        plugin = pl;
        timeSinceLastKOTH = 0;

        pl.getCommand("koth").setExecutor(new KothKCMD());
        pl.getCommand("koth").setTabCompleter(new KothKCMD());

        pl.getServer().getPluginManager().registerEvents(new KEPoints(), pl);

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

    public static void endKOTHEvent() {
        if(!running) return;
        carpetTask.cancel();
        KRunnable.bossBars.forEach(Audience::hideBossBar);
        WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(Bukkit.getWorld("world"))).removeRegion(region.getId());
        CuboidRegion rg = new CuboidRegion(BukkitAdapter.adapt(Bukkit.getWorld("world")), region.getMinimumPoint(), region.getMaximumPoint());
        rg.forEach(bv3 -> {
            if(bv3.getY() == 64) Bukkit.getWorld("world").getBlockAt(bv3.getX(), bv3.getY(), bv3.getZ()).setType(Material.BEDROCK);
            else Bukkit.getWorld("world").getBlockAt(bv3.getX(), bv3.getY(), bv3.getZ()).setType(Material.AIR);
        });
        region = null;
        running = false;
        if(kothData.size() > 3) {
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
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(Utils.format("<yellow><b>The KOTH event has ended"));
                p.sendMessage(Utils.format("<yellow>--- <gold>Top Points <yellow>---"));
                p.sendMessage(Utils.format("<yellow>1. " + topPointsList.get(0).getOffPlayer().getName() + " - " + topPointsList.get(0).getPoints())
                        .hoverEvent(HoverEvent.showText(
                                Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topPointsList.get(0).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topPointsList.get(0).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topPointsList.get(0).getPoints()))
                        ));
                p.sendMessage(Utils.format("<yellow>2. " + topPointsList.get(1).getOffPlayer().getName() + " - " + topPointsList.get(1).getPoints())
                        .hoverEvent(HoverEvent.showText(
                                Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topPointsList.get(1).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topPointsList.get(1).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topPointsList.get(1).getPoints()))
                        ));
                p.sendMessage(Utils.format("<yellow>3. " + topPointsList.get(2).getOffPlayer().getName() + " - " + topPointsList.get(2).getPoints())
                        .hoverEvent(HoverEvent.showText(
                                Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topPointsList.get(2).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topPointsList.get(2).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topPointsList.get(2).getPoints()))
                        ));
                p.sendMessage(" ");
                p.sendMessage(Utils.format("<yellow>--- <gold>Top Kills <yellow>---"));
                p.sendMessage(Utils.format("<yellow>1. " + topKillsList.get(0).getOffPlayer().getName() + " - " + topKillsList.get(0).getKills())
                        .hoverEvent(HoverEvent.showText(
                                Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topKillsList.get(0).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topKillsList.get(0).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topKillsList.get(0).getPoints()))
                        ));
                p.sendMessage(Utils.format("<yellow>2. " + topKillsList.get(1).getOffPlayer().getName() + " - " + topKillsList.get(1).getKills())
                        .hoverEvent(HoverEvent.showText(
                                Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topKillsList.get(1).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topKillsList.get(1).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topKillsList.get(1).getPoints()))
                        ));
                p.sendMessage(Utils.format("<yellow>3. " + topKillsList.get(2).getOffPlayer().getName() + " - " + topKillsList.get(2).getKills())
                        .hoverEvent(HoverEvent.showText(
                                Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>>- <yellow>" + topKillsList.get(2).getKills() + "\n<green>Deaths <gray>>- <yellow>" + topKillsList.get(2).getDeaths() + "\n<green>Points <gray>>- <yellow>" + topKillsList.get(2).getPoints()))
                        ));
            });
        }
        kothData = new HashMap<>();
        timeSinceLastKOTH = System.currentTimeMillis();
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
