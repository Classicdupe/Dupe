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
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;
import xyz.prorickey.proutils.ChatFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KEPoints implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!KOTHEventManager.region.contains(e.getPlayer().getLocation().getBlockX(), e.getPlayer().getLocation().getBlockY(), e.getPlayer().getLocation().getBlockZ())) return;
        KOTHEventManager.PlayerKothData data = KOTHEventManager.getPlayerKothData(e.getPlayer());
        data.addDeath();
        if(e.getPlayer().getKiller() != null) {
            Player killer = e.getPlayer().getKiller();
            KOTHEventManager.PlayerKothData killerData = KOTHEventManager.getPlayerKothData(killer);
            killerData.addKill();
            e.deathMessage(
                    Component.text(ChatFormat.format("&c\u2620 "))
                            .append(Component.text(ChatFormat.format("&6" + e.getPlayer().getName()))
                                    .hoverEvent(HoverEvent.showText(
                                            Component.text(ChatFormat.format("&eKOTH Stats\n&aKills &7- &e" + data.getKills() + "\n&aDeaths &7- &e" + data.getDeaths() + "\n&aPoints &7- &e" + data.getPoints()))
                                    )))
                            .append(Component.text(ChatFormat.format(" &ewas murdered by ")))
                            .append(Component.text(ChatFormat.format("&6" + killer.getName()))
                                    .hoverEvent(HoverEvent.showText(
                                            Component.text(ChatFormat.format("&eKOTH Stats\n&aKills &7- &e" + killerData.getKills() + "\n&aDeaths &7- &e" + killerData.getDeaths() + "\n&aPoints &7- &e" + killerData.getPoints()))
                                    )))
            );
        }
    }

    private static Map<UUID, Long> lastRedPoint = new HashMap<>();
    private static Map<UUID, Long> lastYellowPoint = new HashMap<>();
    private static Map<UUID, Long> lastGreenPoint = new HashMap<>();
    private static Map<UUID, Long> lastPinkEffect = new HashMap<>();
    private static Map<UUID, Long> lastOrangeEffect = new HashMap<>();
    private static final int timeBetween = 15;

    public static class CarpetTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(KOTHEventManager.region.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ())) {
                    KOTHEventManager.PlayerKothData data = KOTHEventManager.getPlayerKothData(p);
                    switch(Bukkit.getWorld("world").getBlockAt(p.getLocation()).getType()) {
                        case RED_CARPET -> {
                            if(!lastRedPoint.containsKey(p.getUniqueId()) || (System.currentTimeMillis()-lastRedPoint.get(p.getUniqueId())) > 1000*timeBetween) {
                                data.addPoints(5);
                                lastRedPoint.put(p.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                        case YELLOW_CARPET -> {
                            if(!lastYellowPoint.containsKey(p.getUniqueId()) || (System.currentTimeMillis()-lastYellowPoint.get(p.getUniqueId())) > 1000*timeBetween) {
                                data.addPoints(4);
                                lastYellowPoint.put(p.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                        case GREEN_CARPET -> {
                            if(!lastGreenPoint.containsKey(p.getUniqueId()) || (System.currentTimeMillis()-lastGreenPoint.get(p.getUniqueId())) > 1000*timeBetween) {
                                data.addPoints(3);
                                lastGreenPoint.put(p.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                        case PINK_CARPET -> {
                            if(!lastPinkEffect.containsKey(p.getUniqueId()) || (System.currentTimeMillis()-lastPinkEffect.get(p.getUniqueId())) > 1000*timeBetween) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30*20, 2));
                                lastPinkEffect.put(p.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                        case ORANGE_CARPET -> {
                            if(!lastOrangeEffect.containsKey(p.getUniqueId()) || (System.currentTimeMillis()-lastOrangeEffect.get(p.getUniqueId())) > 1000*timeBetween) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30*20, 2));
                                lastOrangeEffect.put(p.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                        case END_PORTAL -> {
                            p.teleport(new Location(Bukkit.getWorld("world"), 0, 110, 0));
                            p.setGliding(true);
                        }
                    }
                }
            });
        }
    }

}
