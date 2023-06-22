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
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.arena.commands.ArenaCMD;
import xyz.prorickey.classicdupe.playerevents.arena.events.ArenaFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EventListener;
import java.util.Random;

import static xyz.prorickey.classicdupe.playerevents.KOTHEventManager.loadSchem;

public class ARENAManager {

    private static JavaPlugin plugin;

    public static void init(JavaPlugin pl) {
        plugin = pl;

        pl.getCommand("duel").setExecutor(new ArenaCMD());

        pl.getServer().getPluginManager().registerEvents(new ArenaFunctions(), pl); // We should probably have a single playerEvent events handler?

    }

    public static void startDuel(Player a, Player v){
        String ArenaSchem = getRandomArenaSchem();
        int x = ArenaFunctions.makeX();
        int z = ArenaFunctions.makeZ();
        int attX = 20; //Attacker's spawning X coordinate (relative to schematic's 0,0) // I just set this to random value for testing
        int attY = 0; //Attacker's spawning Y coordinate (relative to schematic's 0,0)
        int attZ = 20; //Attacker's spawning Z coordinate (relative to schematic's 0,0)
        int vicDis = 20; //Distance between attacker's spawning X coordinate and victim's spawning X coordinate
        try {
            loadSchem(ArenaSchem, x, z);
        } catch (IOException | WorldEditException e) {
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("arena.verbose")) p.sendMessage(Utils.cmdMsg(
                        "<red>A duel had an error starting. Cancelling."
                ));
            });
            ArenaFunctions.removeDuelList(a, v);
            a.sendMessage(Utils.cmdMsg("<red>Duel had an error starting."));
            v.sendMessage(Utils.cmdMsg("<red>Duel had an error starting."));
            return;
        }
        Location attLoc = new Location(Bukkit.getWorld("arenaWorld"), attX+x, attY+65, attZ+z);
        Location vicLoc = new Location(Bukkit.getWorld("arenaWorld"), attX+x+vicDis, attY+65, attZ+z);
        a.teleport(attLoc);
        v.teleport(vicLoc);
        // Attack cooldown, like a countdown or something;
    }

    public static void loadSchem(String name, int x, int z) throws IOException, WorldEditException {
        File schemFile = new File(plugin.getDataFolder().getAbsolutePath() + "/schems/" + name + ".schem");
        Clipboard clipboard = ClipboardFormats.findByFile(schemFile).getReader(new FileInputStream(schemFile)).read();
        EditSession ses = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("arenaWorld")));
        Operation op = new ClipboardHolder(clipboard)
                .createPaste(ses)
                .to(BlockVector3.at(x, 65, z))
                .ignoreAirBlocks(true)
                .build();
        Operations.complete(op);
        ses.close();
    }

    public static String getRandomArenaSchem() {
        int randomIndex = new Random().nextInt(Config.getConfig().getStringList("events.arena.schems").size());
        return Config.getConfig().getStringList("events.arena.schems").get(randomIndex);
    }

}
