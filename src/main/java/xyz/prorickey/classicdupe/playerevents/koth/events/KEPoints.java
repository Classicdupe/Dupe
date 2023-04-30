package xyz.prorickey.classicdupe.playerevents.koth.events;

import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;

import java.util.HashSet;
import java.util.Set;


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

    public static final Set<Player> glidingFellas = new HashSet<>();

    @EventHandler
    public void onEntityPortalEnter(EntityPortalEnterEvent e) {
        if(e.getEntityType() != EntityType.PLAYER || !KOTHEventManager.running) return;
        Player p = (Player) e.getEntity();
        glidingFellas.add(p);
        p.teleport(new Location(Bukkit.getWorld("world"), 0, 110, 0));
        p.setGliding(true);
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent e) {
        if(e.getEntityType() != EntityType.PLAYER || !KOTHEventManager.running) return;
        Player p = (Player) e.getEntity();
        if(!glidingFellas.contains(p)) return;
        if(p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) glidingFellas.remove(p);
        else e.setCancelled(true);
    }

}
