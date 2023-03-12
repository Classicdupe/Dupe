package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import xyz.prorickey.classicdupe.Utils;

import java.util.HashMap;
import java.util.Map;

public class GoldenAppleCooldown implements Listener {

    public static Map<Player, Long> lastGappEaten = new HashMap<>();

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) return;
        if(lastGappEaten.containsKey(e.getPlayer()) &&
                lastGappEaten.get(e.getPlayer()) + (60*1000) > System.currentTimeMillis()) {
            Long wait = (lastGappEaten.get(e.getPlayer()) + (60*1000))-System.currentTimeMillis();
            e.getPlayer().sendMessage(Utils.cmdMsg("&cYou must wait " + Math.round(wait/1000) + " second(s) before you can eat another Golden Apple"));
            e.setCancelled(true);
            return;
        }
        lastGappEaten.put(e.getPlayer(), System.currentTimeMillis());
    }

}
