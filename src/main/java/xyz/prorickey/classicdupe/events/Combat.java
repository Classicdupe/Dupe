package xyz.prorickey.classicdupe.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;

import java.util.*;

public class Combat implements Listener {

    public static final Map<Player, Long> inCombat = new HashMap<>();
    public static final Map<Player, Player> whoHitWho = new HashMap<>();

    public static final Map<EnderCrystal, Player> whoKilledCrystal = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(!e.getEntity().getType().equals(EntityType.PLAYER) || e.getEntity().hasMetadata("NPC")) return;
        if(e.getDamager() instanceof Player att && Utils.isVanished(att) && !att.hasPermission("admin.bypassnopvpinvanish")) {
            e.setCancelled(true);
            att.sendMessage(Utils.cmdMsg("<red>You cannot attack players while vanished"));
            return;
        }
        if(e.getEntity() instanceof Player player && JoinEvent.nakedProtection.containsKey(player)) {
            e.setCancelled(true);
            if(e.getDamager() instanceof Player attacker) attacker.sendMessage(Utils.cmdMsg("<red>You cannot attack that player, they are currently in naked protection"));
            return;
        }
        if(e.getDamager() instanceof Player player && JoinEvent.nakedProtection.containsKey(player)) {
            e.setCancelled(true);
            player.sendMessage(Utils.cmdMsg("<red>You cannot attack other people while in naked protection. To disable naked protection execute /nakedoff"));
            return;
        }
        if(e.getEntity() instanceof Player player && e.getDamager() instanceof Player attacker) {
            ClanMember pmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
            ClanMember amem = ClassicDupe.getClanDatabase().getClanMember(attacker.getUniqueId());
            if(pmem != null && amem != null && pmem.getClanID() != null && Objects.equals(pmem.getClanID(), amem.getClanID())) {
                e.setCancelled(true);
                return;
            }
        }
        if(e.getEntity() instanceof Player player && !e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) inCombat.put(player, System.currentTimeMillis());
        if(e.getDamager() instanceof Player player) inCombat.put(player, System.currentTimeMillis());
        if(e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
            whoHitWho.put(victim, attacker);
            whoHitWho.put(attacker, victim);
        }
        if(e.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player shooter && e.getEntity() instanceof Player victim) {
            whoHitWho.put(victim, shooter);
            whoHitWho.put(shooter, victim);
        }
        if(e.getDamager() instanceof Player player && e.getEntity() instanceof EnderCrystal crystal) whoKilledCrystal.put(crystal, player);
        if(e.getDamager() instanceof EnderCrystal crystal && e.getEntity() instanceof Player player && whoKilledCrystal.containsKey(crystal)) {
            whoHitWho.put(player, whoKilledCrystal.get(crystal));
            whoHitWho.put(whoKilledCrystal.get(crystal), player);
        }
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if(Combat.inCombat.containsKey(e.getPlayer()) && e.getMessage().startsWith("/home")) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;
        if(KOTHEventManager.region != null && KOTHEventManager.region.contains(
                e.getEntity().getLocation().getBlockX(),
                e.getEntity().getLocation().getBlockY(),
                e.getEntity().getLocation().getBlockZ())) return;
        if(Combat.inCombat.containsKey(player) && !player.isGliding()) {
            e.setCancelled(true);
            player.sendMessage(Utils.cmdMsg("<red>You cannot use an elytra while in combat"));
        }
    }

    // An interesting approach offered by one of the paper devs
    @EventHandler
    public void onPlayerRiptide(PlayerRiptideEvent e) {
        if(Combat.inCombat.containsKey(e.getPlayer())) {
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>You cannot riptide while in combat"));
            Player player = e.getPlayer();
            Vector vel = player.getVelocity();
            Bukkit.getScheduler().scheduleSyncDelayedTask(ClassicDupe.getPlugin(), () -> player.setVelocity(vel), 1L);
        }
    }

    public static class CombatTask extends BukkitRunnable {
        @Override
        public void run() {
            for(int i = 0; i < Combat.inCombat.size(); i++) {
                Player player = (new ArrayList<>(Combat.inCombat.keySet())).get(i);
                Long time = (new ArrayList<>(Combat.inCombat.values())).get(i);
                if((time + (1000*15)) < System.currentTimeMillis() && Combat.inCombat.containsKey(player)) {
                    Combat.inCombat.remove(player);
                    whoHitWho.remove(player);
                    player.sendActionBar(Utils.format("<green>You are no longer in combat"));
                } else player.sendActionBar(Utils.format("<red>You are currently in combat"));
            }
        }
    }

}
