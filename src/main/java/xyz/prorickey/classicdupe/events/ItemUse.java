package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.customitems.CIKeys;
import xyz.prorickey.classicdupe.customitems.FireballWand;

public class ItemUse implements Listener {

    private final FireballWand fireballWand;

    public ItemUse() {
        this.fireballWand = new FireballWand(ClassicDupe.getPlugin());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            // Player clicked while holding an item
            // Do something here

            if (event.getItem().getItemMeta().getPersistentDataContainer().has(CIKeys.FBWAND, PersistentDataType.STRING)) {
                fireballWand.use(event.getPlayer());
            }
        } else {
            // Player used an item
            // Do something here
        }
    }



    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        fireballWand.playerLeave(event.getPlayer());
    }
}
