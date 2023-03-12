package xyz.prorickey.classicdupe.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.HashMap;
import java.util.Map;

public class Combat implements Listener {

    public static Map<Player, Long> inCombat = new HashMap<>();
    public static Map<Entity, Player> menaces = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player player && JoinEvent.nakedProtection.containsKey(player)) {
            e.setCancelled(true);
            if(e.getDamager() instanceof Player attacker) attacker.sendMessage(Utils.cmdMsg("&cYou cannot attack that player, they are currently in naked protection"));
            return;
        }
        if(e.getDamager() instanceof Player player && JoinEvent.nakedProtection.containsKey(player)) {
            e.setCancelled(true);
            player.sendMessage(Utils.cmdMsg("&cYou cannot attack other people while in naked protection. To disable naked protection execute /nakedoff"));
            return;
        }
        if(e.getEntity() instanceof Player player && !e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) inCombat.put(player, System.currentTimeMillis());
        if(e.getDamager() instanceof Player player) inCombat.put(player, System.currentTimeMillis());
    }

}
