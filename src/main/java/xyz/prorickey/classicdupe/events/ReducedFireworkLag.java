package xyz.prorickey.classicdupe.events;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FireworkExplodeEvent;


public class ReducedFireworkLag implements Listener {

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent e) {
        Firework fw = e.getEntity();
        if(fw.getFireworkMeta().getEffectsSize() > 3) {
            fw.getFireworkMeta().clearEffects();
            fw.getFireworkMeta().addEffects(fw.getFireworkMeta().getEffects().subList(0, 2));
        }
    }

}
