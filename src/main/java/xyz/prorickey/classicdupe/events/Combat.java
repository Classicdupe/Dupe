package xyz.prorickey.classicdupe.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.ClanMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Combat implements Listener {

    public static final Map<Player, Long> inCombat = new HashMap<>();
    public static final Map<Player, Player> whoHitWho = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
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
            ClanMember pmem = ClanDatabase.getClanMember(player.getUniqueId());
            ClanMember amem = ClanDatabase.getClanMember(attacker.getUniqueId());
            if(pmem.getClanID() != null && pmem.getClanID() != null && Objects.equals(pmem.getClanID(), amem.getClanID())) {
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
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if(Combat.inCombat.containsKey(e.getPlayer()) && e.getMessage().startsWith("/home")) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;
        if(Combat.inCombat.containsKey(player) && !player.isGliding()) {
            e.setCancelled(true);
            player.sendMessage(Utils.cmdMsg("<red>You cannot use an elytra while in combat"));
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
