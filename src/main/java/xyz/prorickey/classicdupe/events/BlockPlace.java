package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockPlace implements Listener {

    public static final List<Material> bannedToPlaceBcAnnoyingASF = new ArrayList<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!e.getPlayer().hasPermission("admin.placeBannedBlocks") && bannedToPlaceBcAnnoyingASF.contains(e.getBlock().getType())) e.setCancelled(true);
        if(e.getBlock().getType().equals(Material.DRAGON_EGG)) e.setCancelled(true);
    }

}
