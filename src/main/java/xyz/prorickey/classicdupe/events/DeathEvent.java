package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

public class DeathEvent implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = e.getEntity().getKiller();
        ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getEntity().getUniqueId().toString());
        if(e.getEntity().getKiller() != null) ClassicDupe.getDatabase().getPlayerDatabase().addKill(killer.getUniqueId().toString());
        EntityDamageEvent.DamageCause damageCause = player.getLastDamageCause().getCause();
        switch(damageCause) {
            case BLOCK_EXPLOSION -> {
                if(player.getKiller() != null) e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was exploded by &6" + killer.getName())));
                else e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e exploded")));
            }
            case PROJECTILE -> {
                if(player.getKiller() != null) e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was sniped by &6" + killer.getName())));
                else e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died by a projectile")));
            }
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
                if(player.getKiller() != null) e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was slain by &6" + killer.getName())));
                else e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was slain")));
            }
            case FALL -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e fell to his death")));
            case LAVA -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e took a swim in lava")));
            case VOID -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e jumped into the abyss")));
            case FIRE -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was cremated")));
            case MAGIC -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died to a magic spell")));
            case DRYOUT -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e dried out?")));
            case FREEZE -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e froze to death")));
            case POISON -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was poisoned")));
            case THORNS -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e was pricked by too many thorns")));
            case WITHER -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e withered away")));
            case MELTING -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e melted?")));
            case SUICIDE -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e killed himself")));
            case CRAMMING -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e got Astroworlded")));
            case DROWNING -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e drowned")));
            case FIRE_TICK -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e burned alive")));
            case HOT_FLOOR -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e failed the floor is lava challenge")));
            case LIGHTNING -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e got struck by lightning")));
            case SONIC_BOOM -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died from a sonic boom")));
            case STARVATION -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e starved to death")));
            case SUFFOCATION -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e suffocated")));
            case DRAGON_BREATH -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died from the dragon's breath")));
            case FALLING_BLOCK -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died by a falling block")));
            case FLY_INTO_WALL -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e flew into a wall")));
            case ENTITY_EXPLOSION -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died from an explosion")));
            default -> e.deathMessage(Component.text(Utils.format("&c☠ &6" + player.getName() + "&e died")));
        }
    }

}
