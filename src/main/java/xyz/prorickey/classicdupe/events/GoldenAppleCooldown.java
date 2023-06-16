package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import xyz.prorickey.classicdupe.Utils;

public class GoldenAppleCooldown implements Listener {

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) return;
        if(e.getPlayer().hasCooldown(Material.ENCHANTED_GOLDEN_APPLE))e.getPlayer().sendMessage(Utils.format("<red>Enchanted golden apples are in cooldown."));
        e.getPlayer().setCooldown(Material.ENCHANTED_GOLDEN_APPLE, 60*20);
    }

}