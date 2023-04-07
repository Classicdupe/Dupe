package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.proutils.ChatFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Combat implements Listener {

    public static Map<Player, Long> inCombat = new HashMap<>();
    public static Map<Player, Player> whoHitWho = new HashMap<>();

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
        if(e.getEntity() instanceof Player player && e.getDamager() instanceof Player attacker) {
            ClanDatabase.ClanMember pmem = ClanDatabase.getClanMember(player.getUniqueId());
            ClanDatabase.ClanMember amem = ClanDatabase.getClanMember(attacker.getUniqueId());
            if(Objects.equals(pmem.getClanId(), amem.getClanId())) e.setCancelled(true);
            return;
        }
        if(e.getEntity() instanceof Player player && !e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) inCombat.put(player, System.currentTimeMillis());
        if(e.getDamager() instanceof Player player) inCombat.put(player, System.currentTimeMillis());
        if(e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
            whoHitWho.put(victim, attacker);
            whoHitWho.put(attacker, victim);
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
                    player.sendActionBar(Component.text(ChatFormat.format("&aYou are no longer in combat")));
                } else player.sendActionBar(Component.text(ChatFormat.format("&cYou are currently in combat")));
            }
        }
    }

}
