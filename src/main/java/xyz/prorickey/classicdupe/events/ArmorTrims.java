package xyz.prorickey.classicdupe.events;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.custom.CustomSets;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ArmorTrims implements Listener {

    private static NamespacedKey vexKey = new NamespacedKey(ClassicDupe.getPlugin(), "ownerUUID");

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if(e.getDamager() instanceof Player attacker) {
            if(
                    hasTrimSet(attacker, TrimPattern.VEX) &&
                            (attacker.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_SWORD) ||
                                    attacker.getInventory().getItemInMainHand().getType().equals(Material.STONE_SWORD) ||
                                    attacker.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD) ||
                                    attacker.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_SWORD) ||
                                    attacker.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD) ||
                                    attacker.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_SWORD))
            ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.vex.swordAttackMultiplier"));
            else if(
                    hasTrimSet(attacker, TrimPattern.COAST) &&
                            attacker.getInventory().getItemInMainHand().getType().equals(Material.TRIDENT)
            ) {
                e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.coast.tridentAttackMultiplier"));
                if(new Random().nextDouble() <= 0.02) e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
            } else if(
                    hasTrimSet(attacker, TrimPattern.EYE) &&
                            e.getEntity() instanceof LivingEntity entity
            ) {
                double r = new Random().nextDouble();
                if(r <= 0.1) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15*20, 1));
                } else if(r >= 0.95) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10*20, 1));
                }
            } else if(
                    hasTrimSet(attacker, TrimPattern.HOST)
            ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.host.attackMultiplier"));
            else if(
                    hasTrimSet(attacker, TrimPattern.RAISER) &&
                            new Random().nextDouble() <= 0.25
            ) e.getEntity().getWorld().spawn(e.getEntity().getLocation(), EvokerFangs.class, fangs -> fangs.setOwner(attacker));
            else if(
                    hasTrimSet(attacker, TrimPattern.RIB) &&
                            e.getEntity() instanceof LivingEntity entity &&
                            new Random().nextDouble() <= 0.05
            ) entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10*20, 1));
            else if(
                    hasTrimSet(attacker, TrimPattern.SHAPER) &&
                            new Random().nextDouble() <= 0.1 &&
                            e.getEntity() instanceof LivingEntity entity
            ) {
                entity.getWorld().spawn(entity.getLocation(), Vex.class, vex -> {
                    vex.setTarget(entity);
                    vex.setLimitedLifetime(true);
                    vex.setLimitedLifetimeTicks(20 * 30);
                    vex.getPersistentDataContainer().set(vexKey, PersistentDataType.STRING, attacker.getUniqueId().toString());
                });
            }
            else if(
                    hasTrimSet(attacker, TrimPattern.SNOUT) &&
                            e.getEntity() instanceof LivingEntity entity &&
                            new Random().nextDouble() <= 0.05
            ) entity.setFireTicks(20 * 8);
            else if(
                    (hasTrimSet(attacker, TrimPattern.SPIRE) || hasTrimSet(attacker, TrimPattern.TIDE)) &&
                            e.getCause().equals(EntityDamageEvent.DamageCause.THORNS)
            ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.spire.thornsAttackMultiplier"));
            else if(
                    hasTrimSet(attacker, TrimPattern.TIDE) &&
                            e.getEntity() instanceof LivingEntity entity &&
                            new Random().nextDouble() <= 0.25
            ) entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 25*20, 1));
            else if(
                    hasTrimSet(attacker, TrimPattern.WARD)
            ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.ward.attackMultiplier"));
            else if(
                    hasTrimSet(attacker, TrimPattern.WILD) &&
                            e.getEntity() instanceof LivingEntity entity
            ) {
                double r = new Random().nextDouble();
                if(r <= 0.05) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10*20, 1));
                } else if(r >= 0.9) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10*20, 1));
                }
            }
        }

        if(e.getEntity() instanceof Player victim) {
            if(
                    hasTrimSet(victim, TrimPattern.DUNE) &&
                            new Random().nextDouble() <= 0.1
            ) e.setDamage(0);
            else if(
                    hasTrimSet(victim, TrimPattern.HOST)
            ) {
                double r = new Random().nextDouble();
                if(r <= 0.02) {
                    e.setDamage(e.getDamage() + 8d);
                } else if(r >= 0.9) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 1));
                    e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.host.defenseMultiplier"));
                } else {
                    e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.host.defenseMultiplier"));
                }
            } else if(
                    hasTrimSet(victim, TrimPattern.RAISER) &&
                            new Random().nextDouble() <= 0.25 &&
                            e.getDamager() instanceof LivingEntity entity
            ) entity.damage(e.getDamage() * Config.getConfig().getDouble("trimset.raiser.reflectMultiplier"));
            else if(
                    hasTrimSet(victim, TrimPattern.WARD)
            ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.ward.defenseMultiplier"));
            else if(
                    hasTrimSet(victim, TrimPattern.WAYFINDER) &&
                            new Random().nextDouble() <= 0.25
            ) {
                if(victim.getPotionEffect(PotionEffectType.SPEED) == null) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 1));
                } else if(victim.getPotionEffect(PotionEffectType.SPEED).getAmplifier() == 1) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 2));
                } else if(victim.getPotionEffect(PotionEffectType.SPEED).getAmplifier() == 2) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 3));
                }
            }
        }

        if(e.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player attacker) {
            if(
                    hasTrimSet(attacker, TrimPattern.SENTRY)
            ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.sentry.arrowAttackMultiplier"));
        }

        if(
                e.getDamager() instanceof Vex vex &&
                        vex.getPersistentDataContainer().has(vexKey, PersistentDataType.STRING) &&
                        vex.getPersistentDataContainer().get(vexKey, PersistentDataType.STRING).equals(e.getEntity().getUniqueId().toString())
        ) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player victim)) return;
        if(
                hasTrimSet(victim, TrimPattern.SNOUT) &&
                        e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)
        ) e.setCancelled(true);
        else if(
                hasTrimSet(victim, TrimPattern.SNOUT) &&
                        e.getCause().equals(EntityDamageEvent.DamageCause.FIRE)
        ) e.setDamage(e.getDamage() * Config.getConfig().getDouble("trimset.snout.fireDamageMultiplier"));
    }

    @EventHandler
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent e) {
        if(!(e.getEntity() instanceof Player victim)) return;
        if(
                hasTrimSet(victim, TrimPattern.WARD)
        ) e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(Config.getConfig().getDouble("trimset.ward.knockbackMultiplier")));
    }

    public static class ArmorTrimsTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(hasTrimSet(player, TrimPattern.VEX)) player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5*20, 1));
                else if(hasTrimSet(player, TrimPattern.HOST)) player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5*20, 1));
                else if(hasTrimSet(player, TrimPattern.WARD)) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
            });
        }
    }

    public static Boolean hasTrimSet(Player player, TrimPattern pattern) {
        if(player.getInventory().getHelmet() == null ||
                !(player.getInventory().getHelmet().getItemMeta() instanceof ArmorMeta helmetMeta) ||
                !helmetMeta.hasTrim() ||
                !helmetMeta.getTrim().getPattern().equals(pattern)) return false;
        TrimMaterial trimMat = helmetMeta.getTrim().getMaterial();
        return player.getInventory().getChestplate() != null &&
                (player.getInventory().getChestplate().getItemMeta() instanceof ArmorMeta chestMeta) &&
                chestMeta.hasTrim() &&
                chestMeta.getTrim().getPattern().equals(pattern) &&
                chestMeta.getTrim().getMaterial().equals(trimMat) &&

                player.getInventory().getLeggings() != null &&
                (player.getInventory().getLeggings().getItemMeta() instanceof ArmorMeta legMeta) &&
                legMeta.hasTrim() &&
                legMeta.getTrim().getPattern().equals(pattern) &&
                legMeta.getTrim().getMaterial().equals(trimMat) &&

                player.getInventory().getBoots() != null &&
                (player.getInventory().getBoots().getItemMeta() instanceof ArmorMeta bootsMeta) &&
                bootsMeta.hasTrim() &&
                bootsMeta.getTrim().getPattern().equals(pattern) &&
                bootsMeta.getTrim().getMaterial().equals(trimMat);
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {

        if(event.getInventory().getInputEquipment() != null || Objects.requireNonNull(event.getInventory().getInputTemplate()).getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
            CustomSets.keys.forEach(key -> {
                if(
                        event.getInventory().getInputEquipment().getItemMeta().getPersistentDataContainer().has(key) &&
                                !CustomSets.keysToSets.get(key).getSmithable()
                ) event.setResult(null);
            });
        } else if(
                (event.getInventory().getInputEquipment() != null || event.getInventory().getInputTemplate().getType() != Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) &&
                        !(event.getInventory().getInputEquipment().getType().equals(Material.NETHERITE_HELMET) ||
                                event.getInventory().getInputEquipment().getType().equals(Material.NETHERITE_CHESTPLATE) ||
                                event.getInventory().getInputEquipment().getType().equals(Material.NETHERITE_LEGGINGS) ||
                                event.getInventory().getInputEquipment().getType().equals(Material.NETHERITE_BOOTS))
        ) {
            event.setResult(null);
        }

    }

}
