package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.units.qual.C;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.proutils.ChatFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeathEvent implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(!e.isBedSpawn() && !e.isAnchorSpawn()) {
            if(e.getPlayer().getWorld().getName().equals("world_nether")) e.setRespawnLocation(ClassicDupe.getDatabase().getNetherSpawn());
            else e.setRespawnLocation(ClassicDupe.getDatabase().getSpawn());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Combat.inCombat.remove(player);
        Player killer = e.getEntity().getKiller();
        ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getEntity().getUniqueId().toString());
        if(e.getEntity().getKiller() != null && e.getEntity().getKiller() != player) {
            if(killer != player) ClassicDupe.getDatabase().getPlayerDatabase().addKill(killer.getUniqueId().toString());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
            meta.setOwningPlayer(e.getEntity());
            meta.displayName(Component.text(ChatFormat.format("&e" + e.getEntity().getName() + "'s Head")));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            meta.lore(List.of(
                    Component.text(ChatFormat.format("&cKilled by &6" + killer.getName())),
                    Component.text(ChatFormat.format("&cKilled on " + dtf.format(now)))
            ));
            skull.setItemMeta(meta);
            e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), skull);
        }
        EntityDamageEvent.DamageCause damageCause = player.getLastDamageCause().getCause();
        switch(damageCause) {
            case BLOCK_EXPLOSION -> {
                if(player.getKiller() != null) e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was exploded by &6" + killer.getName())));
                else e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e exploded")));
            }
            case PROJECTILE -> {
                if(player.getKiller() != null) e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was sniped by &6" + killer.getName())));
                else e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died by a projectile")));
            }
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
                if(player.getKiller() != null) e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was slain by &6" + killer.getName())));
                else e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was slain")));
            }
            case FALL -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e fell to their death")));
            case LAVA -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e took a swim in lava")));
            case VOID -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e jumped into the abyss")));
            case FIRE -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was cremated")));
            case MAGIC -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died to a magic spell")));
            case DRYOUT -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e dried out?")));
            case FREEZE -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e froze to death")));
            case POISON -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was poisoned")));
            case THORNS -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e was pricked by too many thorns")));
            case WITHER -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e withered away")));
            case MELTING -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e melted?")));
            case SUICIDE -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e killed themself")));
            case CRAMMING -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e got Astroworlded")));
            case DROWNING -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e drowned")));
            case FIRE_TICK -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e burned alive")));
            case HOT_FLOOR -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e failed the floor is lava challenge")));
            case LIGHTNING -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e got struck by lightning")));
            case SONIC_BOOM -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died from a sonic boom")));
            case STARVATION -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e starved to death")));
            case SUFFOCATION -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e suffocated")));
            case DRAGON_BREATH -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died from the dragon's breath")));
            case FALLING_BLOCK -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died by a falling block")));
            case FLY_INTO_WALL -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e flew into a wall")));
            case ENTITY_EXPLOSION -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died from an explosion")));
            default -> e.deathMessage(Component.text(ChatFormat.format("&c\u2620 &6" + player.getName() + "&e died")));
        }
    }

}
