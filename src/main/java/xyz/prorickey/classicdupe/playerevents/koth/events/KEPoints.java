package xyz.prorickey.classicdupe.playerevents.koth.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KEPoints implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!KOTHEventManager.running) return;
        if(!KOTHEventManager.region.contains(e.getPlayer().getLocation().getBlockX(), e.getPlayer().getLocation().getBlockY(), e.getPlayer().getLocation().getBlockZ())) return;
        KOTHEventManager.PlayerKothData data = KOTHEventManager.getPlayerKothData(e.getPlayer());
        data.addDeath();
        if(e.getPlayer().getKiller() != null) {
            Player killer = e.getPlayer().getKiller();
            KOTHEventManager.PlayerKothData killerData = KOTHEventManager.getPlayerKothData(killer);
            killerData.addKill();
            e.deathMessage(
                    Utils.format("<red>\u2620 ")
                            .append(Utils.format("<gold>>" + e.getPlayer().getName())
                                    .hoverEvent(HoverEvent.showText(
                                            Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>- <yellow>" + data.getKills() + "\n<green>Deaths <gray>- <yellow>" + data.getDeaths() + "\n<green>Points <gray>- <yellow>" + data.getPoints()))
                                    ))
                            .append(Utils.format(" <yellow>was murdered by "))
                            .append(Utils.format("<gold>>" + killer.getName())
                                    .hoverEvent(HoverEvent.showText(
                                            Utils.format("<yellow>KOTH Stats\n<green>Kills <gray>- <yellow>" + killerData.getKills() + "\n<green>Deaths <gray>- <yellow>" + killerData.getDeaths() + "\n<green>Points <gray>- <yellow>" + killerData.getPoints()))
                                    ))
            );
        }
    }

}
