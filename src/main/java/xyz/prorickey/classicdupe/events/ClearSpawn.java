package xyz.prorickey.classicdupe.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ClearSpawn implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(e.getPlayer().getWorld()));
        ProtectedRegion region = regions.getRegion("clearspawn");
        if(region.contains(BukkitAdapter.asBlockVector(e.getBlock().getLocation()))) {
            blockMap.remove(e.getBlockReplacedState().getBlock());
            blockMap.put(e.getBlock(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(e.getPlayer().getWorld()));
        ProtectedRegion region = regions.getRegion("clearspawn");
        if(region.contains(BukkitAdapter.asBlockVector(e.getBlock().getLocation()))) blockMap.remove(e.getBlock());
    }

    private static int timeBeforeDestroy = 1000*60*5;

    private static Map<Block, Long> blockMap = new HashMap<>();

    public static class ClearSpawnTask extends BukkitRunnable {
        @Override
        public void run() {
            blockMap.forEach((block, time) -> {
                if((time + timeBeforeDestroy) < System.currentTimeMillis()) {
                    block.setType(Material.AIR);
                    blockMap.remove(block);
                }
            });
        }
    }

}
