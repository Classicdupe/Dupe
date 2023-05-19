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

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) return;
        if(e.getPlayer().hasCooldown(Material.ENCHANTED_GOLDEN_APPLE))e.getPlayer().sendMessage(Utils.format("<red>Enchanted golden apples are in cooldown."));
        int cooldown = 2200;
        if(e.getPlayer().hasPermission("perks.godAppleCooldown.vip")) cooldown = 1000;
        else if(e.getPlayer().hasPermission("perks.godAppleCooldown.mvp")) cooldown = 800;
        else if(e.getPlayer().hasPermission("perks.godAppleCooldown.legend")) cooldown = 600;
        e.getPlayer().setCooldown(Material.ENCHANTED_GOLDEN_APPLE, cooldown);
    }

}
