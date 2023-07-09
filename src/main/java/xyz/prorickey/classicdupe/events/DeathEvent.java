package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.database.PlayerData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeathEvent implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(!e.isBedSpawn() && !e.isAnchorSpawn()) {
            if(e.getPlayer().getWorld().getName().equals("world_nether")) e.setRespawnLocation(ClassicDupe.getDatabase().getSpawn("nether"));
            else e.setRespawnLocation(ClassicDupe.getDatabase().getSpawn("overworld"));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Combat.inCombat.remove(player);
        Player killer = e.getEntity().getKiller();
        ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getEntity().getUniqueId().toString());
        ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getEntity().getUniqueId()).setKillStreak(0);
        if(e.getEntity().getKiller() != null && e.getEntity().getKiller() != player) {
            if(killer != null && killer.getUniqueId() != player.getUniqueId()) {
                ClassicDupe.getDatabase().getPlayerDatabase().addKill(killer.getUniqueId().toString());
                ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(killer.getUniqueId()).addKillStreak(1);
                ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(killer.getUniqueId()).addBalance(Config.getConfig().getInt("economy.moneyMaking.kill"));
                if(ClassicDupe.getDatabase().getBountyDatabase().getBounty(player.getUniqueId()) != null) {
                    ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(killer.getUniqueId())
                            .addBalance(ClassicDupe.getDatabase().getBountyDatabase().getBounty(player.getUniqueId()));
                    ClassicDupe.getDatabase().getBountyDatabase().deleteBounty(player.getUniqueId());
                }
                if(ClassicDupe.getClanDatabase().getClanMember(killer.getUniqueId()).getClanID() != null) ClassicDupe
                        .getClanDatabase()
                        .addClanKill(
                                ClassicDupe.getClanDatabase()
                                        .getClan(ClassicDupe.getClanDatabase().getClanMember(killer.getUniqueId()).getClanID()));
            }
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
            meta.setOwningPlayer(e.getEntity());
            meta.displayName(Utils.format("<yellow>" + e.getEntity().getName() + "'s Head"));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            meta.lore(List.of(
                    Utils.format("<red>Killed by <gold>" + killer.getName()),
                    Utils.format("<red>Killed on " + dtf.format(now))
            ));
            skull.setItemMeta(meta);
            e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), skull);
        }
        EntityDamageEvent.DamageCause damageCause = player.getLastDamageCause().getCause();
        Component weapon = null;
        if(killer != null &&
        killer.getInventory().getItemInMainHand().getItemMeta() != null &&
        killer.getInventory().getItemInMainHand().getItemMeta().displayName() != null) weapon = killer.getInventory().getItemInMainHand().getItemMeta().displayName();
        switch(damageCause) {
            case BLOCK_EXPLOSION, ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
                if(killer != null) {
                    Component message = Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> was murdered by <gold>" + killer.getName());
                    if(weapon != null) message = message.append(Utils.format("<yellow> using ").append(weapon));
                    e.deathMessage(message);
                }
                else e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> was slain"));
            }
            case PROJECTILE -> {
                if(killer != null) {
                    Component message = Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> was sniped by <gold>" + killer.getName());
                    if(weapon != null) message = message.append(Utils.format("<yellow> using ").append(weapon));
                    e.deathMessage(message);
                }
                else e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died by a projectile"));
            }
            case FALL -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> fell to their death"));
            case LAVA -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> took a swim in lava"));
            case VOID -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> jumped into the abyss"));
            case FIRE -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> was cremated"));
            case MAGIC -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died to a magic spell"));
            case DRYOUT -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> dried out?"));
            case FREEZE -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> froze to death"));
            case POISON -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> was poisoned"));
            case THORNS -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> was pricked by too many thorns"));
            case WITHER -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> withered away"));
            case MELTING -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> melted?"));
            case SUICIDE -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> killed themself"));
            case CRAMMING -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> got Astroworlded"));
            case DROWNING -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> drowned"));
            case FIRE_TICK -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> burned alive"));
            case HOT_FLOOR -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> failed the floor is lava challenge"));
            case LIGHTNING -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> got struck by lightning"));
            case SONIC_BOOM -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died from a sonic boom"));
            case STARVATION -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> starved to death"));
            case SUFFOCATION -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> suffocated"));
            case DRAGON_BREATH -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died from the dragon's breath"));
            case FALLING_BLOCK -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died by a falling block"));
            case FLY_INTO_WALL -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> flew into a wall"));
            case ENTITY_EXPLOSION -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died from an explosion"));
            default -> e.deathMessage(Utils.format("<red>\u2620 <gold>" + player.getName() + "<yellow> died"));
        }

        Component comp = e.deathMessage();
        e.deathMessage(null);
        Bukkit.getOnlinePlayers().forEach(p -> {
            PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId());
            if(playerData.getDeathMessages()) p.sendMessage(comp);
        });
    }

}
