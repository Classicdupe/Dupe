package xyz.prorickey.classicdupe.customitems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FireballWand {
    private final Plugin plugin;
    private List<List<String>> cooldown = new ArrayList<>();

    private static final long COOLDOWN_DURATION = 10_000L; // 10 seconds

    public FireballWand(Plugin plugin) {
        this.plugin = plugin;

    }

    public void use(Player player) {
        if (isOnCooldown(player)) {

            return;
        }

        // Player is not on cooldown, perform the action

        // Get the player's location and direction
        Vector direction = player.getLocation().getDirection();

        // Create a fireball entity at the player's location
        Fireball fireball = player.launchProjectile(Fireball.class, direction);

        // Set the fireball's speed (3 blocks per second)
        double speed = 3.0;
        Vector velocity = direction.multiply(speed);
        fireball.setVelocity(velocity);

        // Set the fireball to explode on impact
        fireball.setIsIncendiary(true);
        fireball.setYield(7.0f); // Explosion power

        // Example: Set the shooter of the fireball to the player
        fireball.setShooter(player);

        // Add the player to the cooldowns map
        resetcd(player);
    }

    private void resetcd(Player player) {
        for (List<String> info : cooldown) {
            if (info.get(0).equals(player.getName())) {
                info.set(1, String.valueOf(System.currentTimeMillis()));
                return;
            }
        }

        //since its still going there isn't an entry for the player, so we add one
        List<String> info = new ArrayList<>();
        info.add(player.getName());
        info.add(String.valueOf(System.currentTimeMillis()));
        cooldown.add(info);
        return;
    }



    private boolean isOnCooldown(Player player) {
        if (player.hasPermission("admin.bypassCustomItemCd")) return false;
        String playerName = player.getName();

        for (List<String> info : cooldown) {
            if (info.get(0).equals(playerName)) {
                long lastUsedTime = Long.parseLong(info.get(1));
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - lastUsedTime;

                if (elapsedTime < COOLDOWN_DURATION) {
                    // Player is still on cooldown
                    System.out.println("PLR " + elapsedTime/1000);
                    player.sendMessage("You are on cooldown for " + (COOLDOWN_DURATION - elapsedTime)/1000 + " seconds.");
                    return true;
                } else {
                    // Cooldown has expired, remove the entry from the list
                    for (List<String> info2 : cooldown) {
                        if (info2.get(0).equals(player.getName())) {
                            cooldown.remove(info2);
                            return false;
                        }
                    }
                    return false;
                }
            }
        }

        return false;
    }


    public void playerLeave(Player player) {
        for (List<String> info : cooldown) {
            if (info.get(0).equals(player.getName())) {
                cooldown.remove(info);
                return;
            }
        }
    }
}
