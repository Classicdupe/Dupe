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
import xyz.prorickey.classicdupe.customitems.*;

public class ItemUse implements Listener {

    private final FireballWand fireballWand;
    private final BurstBow burstBow;
    private final PVPPot pvpPot;
    private final PVPPot2 pvpPot2;

    public ItemUse() {
        this.fireballWand = new FireballWand(ClassicDupe.getPlugin());
        this.burstBow = new BurstBow(ClassicDupe.getPlugin());
        this.pvpPot = new PVPPot(ClassicDupe.getPlugin());
        this.pvpPot2 = new PVPPot2(ClassicDupe.getPlugin());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            // Player clicked while holding an item
            // Do something here

            if (event.getItem().getItemMeta().getPersistentDataContainer().has(CIKeys.FBWAND, PersistentDataType.STRING)) {
                fireballWand.use(event.getPlayer());
            }
            //check if its
            if (event.getItem().getItemMeta().getPersistentDataContainer().has(CIKeys.BURSTBOW, PersistentDataType.STRING)) {
                burstBow.use(event.getPlayer());
            }
            //check if its PVPPot
            if (event.getItem().getItemMeta().getPersistentDataContainer().has(CIKeys.PVPPOT, PersistentDataType.STRING)) {
                pvpPot.use(event.getPlayer());
            }
            if (event.getItem().getItemMeta().getPersistentDataContainer().has(CIKeys.PVPPOT2, PersistentDataType.STRING)) {
                pvpPot2.use(event.getPlayer());
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
