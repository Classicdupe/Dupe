package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.customitems.FireballWand;

public class ItemUse implements Listener {

    private FireballWand fireballWand = new FireballWand(ClassicDupe.getPlugin());

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            // Player clicked while holding an item
            // Do something here

            if (event.getItem().getLore().get(0).toString().equals("A wand that shoots fireballs")) {
                fireballWand.use(event.getPlayer());
            }
        } else {
            // Player used an item
            // Do something here
        }
    }

    @EventHandler
    public void onTick() {
        fireballWand.tick();
    }

    //Leave
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        fireballWand.PlayerLeave(e.getPlayer());
    }
}
