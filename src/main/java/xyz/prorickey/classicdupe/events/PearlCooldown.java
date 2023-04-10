package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.prorickey.classicdupe.Utils;

import java.util.HashMap;
import java.util.Map;

public class PearlCooldown implements Listener {

    private static int cooldown = 45*1000;
    private static Map<Player, Long> pearlCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getMaterial().equals(Material.ENDER_PEARL)) {
            if(pearlCooldown.containsKey(e.getPlayer()) && (pearlCooldown.get(e.getPlayer())+30) > System.currentTimeMillis()) {
                e.setCancelled(true);
                Long wait = (pearlCooldown.get(e.getPlayer()) + (cooldown))-System.currentTimeMillis();
                e.getPlayer().sendMessage(Utils.cmdMsg("<red>You cannot throw a pearl for another " + Math.round(wait/1000) + " second(s)"));
            } else pearlCooldown.put(e.getPlayer(), System.currentTimeMillis());
        }
    }

}
