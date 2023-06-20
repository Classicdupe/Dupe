package xyz.prorickey.classicdupe.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import xyz.prorickey.classicdupe.Config;

public class FlowEvent implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        if(Config.getConfig().getBoolean("dev")) return;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion spawnb = container.get(BukkitAdapter.adapt(Bukkit.getWorld("world"))).getRegion("spawnb");
        Location loc = e.getToBlock().getLocation();
        if(spawnb.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) e.setCancelled(true);
    }

}
