package xyz.prorickey.classicdupe.customitems;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FireballWand {
    private final Plugin plugin;


    private List<List<String>> cooldowns = new ArrayList<>();


    public FireballWand(Plugin plugin) {
        this.plugin = plugin;



    }

    public void use(Player plr) {

        if (isOnDelay(plr.getName())) {
            System.out.println(isOnDelay(plr.getName()));
            System.out.println(cooldowns);
            plr.sendMessage("You are on cooldown!");

            return;
        } else {
            System.out.println(cooldowns);

            // Get the player's location and direction
            Vector direction = plr.getLocation().getDirection();

            // Create a fireball entity at the player's location
            Fireball fireball = plr.launchProjectile(Fireball.class, direction);

            // Set the fireball's speed (3 blocks per second)
            double speed = 3.0;
            Vector velocity = direction.multiply(speed);
            fireball.setVelocity(velocity);

            // Set the fireball to explode on impact
            fireball.setIsIncendiary(true);
            fireball.setYield(1.0f); // Explosion power

            // You can also set other properties of the fireball here

            // Example: Set the shooter of the fireball to the player
            fireball.setShooter(plr);
            if (!plr.hasPermission("admin.bypassCustomItemCd")) {

                boolean found = false;
                for (List<String> pack : cooldowns) {
                    if (pack.get(0).equals(plr.getName())) {
                        List<String> oldpack = pack;
                        pack.set(1, "0");

                        //set the old list to the new list in cooldowns
                        cooldowns.set(cooldowns.indexOf(oldpack), pack);
                        found = true;
                    }
                }
                if (found == false) {

                    List<String> newpack = new ArrayList<>();
                    newpack.add(plr.getName());
                    newpack.add("0");
                    cooldowns.add(newpack);

                }
            } else {

            }
        }
    }

    public void tick() {
        for (List<String> pack : cooldowns) {
            String user = pack.get(0);
            int tick = Integer.parseInt(pack.get(1));


            tick++;
            List<String> oldpack = pack;
            pack.set(1, String.valueOf(tick));

            //set the old list to the new list in cooldowns
            cooldowns.set(cooldowns.indexOf(oldpack), pack);

        }
    }

    private boolean isOnDelay(String user) {


        for (List<String> pack : cooldowns) {
            if (pack.get(0).equals(user)) {
                int ticks = Integer.parseInt(pack.get(1));

                if (ticks > 20*10-1) {
                    return false;
                } else {
                    return true;
                }

            }
        }


        return false;
    }

    public void PlayerLeave(Player plr) {
        for (List<String> pack : cooldowns) {
            if (pack.get(0).equals(plr.getName())) {

                cooldowns.remove(pack);
            }
        }
    }




}