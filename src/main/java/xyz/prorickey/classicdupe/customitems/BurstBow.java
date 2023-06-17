package xyz.prorickey.classicdupe.customitems;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BurstBow {
    private final Plugin plugin;
    private List<List<String>> cooldown = new ArrayList<>();

    private static final long COOLDOWN_DURATION = 20_000L; // 20 seconds

    public BurstBow(Plugin plugin) {
        this.plugin = plugin;

    }

    public void use(Player player) {
        if (isOnCooldown(player)) {

            return;
        }
        //loop 3 times
        if (isOnCooldown(player)) {
            return;
        }

        // Loop 3 times
        for (int i = 0; i < 3; i++) {
            // Schedule the arrow shoot with a delay of 5 ticks
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Player is not on cooldown, perform the action

                // Get the player's location and direction
                Vector direction = player.getLocation().getDirection();

                // Create an arrow entity at the player's location
                Arrow arrow = player.launchProjectile(Arrow.class, direction);

                // Set the arrow's speed (3 blocks per second)
                double speed = 3.0;
                Vector velocity = direction.multiply(speed);
                arrow.setVelocity(velocity);

                // Example: Set the shooter of the arrow to the player
                arrow.setShooter(player);
            }, 3 * (i + 1));
        }

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
                    //make a action bar message with the time left
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent("§cYou are on cooldown for " + (COOLDOWN_DURATION - elapsedTime)/1000 + " seconds"));
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
