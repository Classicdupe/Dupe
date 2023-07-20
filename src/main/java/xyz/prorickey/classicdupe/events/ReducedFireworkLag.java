package xyz.prorickey.classicdupe.events;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.meta.FireworkMeta;


public class ReducedFireworkLag implements Listener {

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent e) {
        Firework fw = e.getEntity();
        if(fw.getFireworkMeta().getEffectsSize() > 2) {
            e.setCancelled(true);
            FireworkMeta meta = fw.getFireworkMeta();
            meta.clearEffects();
            meta.addEffects(fw.getFireworkMeta().getEffects().subList(0, 2));
            fw.setFireworkMeta(meta);
            fw.detonate();
        }
    }

}
