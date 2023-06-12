package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.ClanMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Combat implements Listener {

    public static final Map<Player, Long> inCombat = new HashMap<>();
    public static final Map<Player, Player> whoHitWho = new HashMap<>();

    public static final Map<EnderCrystal, Player> whoKilledCrystal = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        System.out.println(e.getEntity());
        System.out.println(e.getDamager());
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

        // Armor Trims
        if(!(e.getDamager() instanceof Player attacker)) return;
        if(
                hasTrimSet(attacker, TrimPattern.VEX) &&
                        (attacker.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_SWORD) ||
                        attacker.getInventory().getItemInMainHand().getType().equals(Material.STONE_SWORD) ||
                        attacker.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD) ||
                        attacker.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_SWORD) ||
                        attacker.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD) ||
                        attacker.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_SWORD))
        ) {
            attacker.sendMessage(Utils.cmdMsg("<green>Vex trim set"));
            e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.vex.swordAttackMultiplier"));
        }

    }

    public static Boolean hasTrimSet(Player player, TrimPattern pattern) {
        if(player.getInventory().getHelmet() == null ||
                !((ArmorMeta) player.getInventory().getHelmet().getItemMeta()).hasTrim() ||
                !((ArmorMeta) player.getInventory().getHelmet().getItemMeta()).getTrim().getPattern().equals(pattern)) return false;
        TrimMaterial trimMat = ((ArmorMeta) player.getInventory().getHelmet().getItemMeta()).getTrim().getMaterial();
        return player.getInventory().getChestplate() != null &&
                ((ArmorMeta) player.getInventory().getChestplate().getItemMeta()).hasTrim() &&
                ((ArmorMeta) player.getInventory().getChestplate().getItemMeta()).getTrim().getPattern().equals(pattern) &&
                ((ArmorMeta) player.getInventory().getChestplate().getItemMeta()).getTrim().getMaterial().equals(trimMat) &&

                player.getInventory().getLeggings() != null &&
                ((ArmorMeta) player.getInventory().getLeggings().getItemMeta()).hasTrim() &&
                ((ArmorMeta) player.getInventory().getLeggings().getItemMeta()).getTrim().getPattern().equals(pattern) &&
                ((ArmorMeta) player.getInventory().getLeggings().getItemMeta()).getTrim().getMaterial().equals(trimMat) &&

                player.getInventory().getBoots() != null &&
                ((ArmorMeta) player.getInventory().getBoots().getItemMeta()).hasTrim() &&
                ((ArmorMeta) player.getInventory().getBoots().getItemMeta()).getTrim().getPattern().equals(pattern) &&
                ((ArmorMeta) player.getInventory().getBoots().getItemMeta()).getTrim().getMaterial().equals(trimMat);
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
