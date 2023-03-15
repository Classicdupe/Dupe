package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.HashMap;
import java.util.Map;

public class GoldenAppleCooldown implements Listener {

    public static Map<Player, Long> lastGappEaten = new HashMap<>();

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) return;
        int cooldown = 45;
        switch(ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()) {
            case "legend" -> cooldown = 10;
            case "mvp" -> cooldown = 20;
            case "vip" -> cooldown = 30;
        }
        if(lastGappEaten.containsKey(e.getPlayer()) && lastGappEaten.get(e.getPlayer()) + (cooldown*1000) > System.currentTimeMillis()) {
            Long wait = (lastGappEaten.get(e.getPlayer()) + (cooldown*1000))-System.currentTimeMillis();
            e.getPlayer().sendMessage(Utils.cmdMsg("&cYou must wait " + Math.round(wait/1000) + " second(s) before you can eat another Golden Apple"));
            e.setCancelled(true);
            return;
        }
        lastGappEaten.put(e.getPlayer(), System.currentTimeMillis());
    }

}
