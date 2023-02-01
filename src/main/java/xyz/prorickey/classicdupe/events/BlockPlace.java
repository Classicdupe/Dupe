package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlace implements Listener {

    List<Material> bannedToPlaceBcAnnoyingASF = List.of(
            Material.BEDROCK,
            Material.BARRIER,
            Material.END_PORTAL_FRAME,
            Material.ENDER_DRAGON_SPAWN_EGG
    );

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!e.getPlayer().hasPermission("admin.placeBannedBlocks") && bannedToPlaceBcAnnoyingASF.contains(e.getBlock().getType())) e.setCancelled(true);
    }

}
