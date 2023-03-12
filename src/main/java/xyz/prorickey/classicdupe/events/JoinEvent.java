package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.util.*;
import java.util.stream.Collectors;

public class JoinEvent implements Listener {

    public static Map<Player, RandomItemTask> randomTaskMap = new HashMap<>();
    public static List<Player> randomItemList = new ArrayList<>();
    public static Map<Player, Long> nakedProtection = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(e.getPlayer().getUniqueId().toString()) == null) {
            ClassicDupe.getDatabase().getPlayerDatabase().initPlayer(e.getPlayer());
            e.getPlayer().teleport(ClassicDupe.getDatabase().spawn);
            e.joinMessage(Component.text(Utils.format("&e" + e.getPlayer().getName() + " &aJust joined for the first time! Give them a warm welcome")));
            RandomItemTask task = new RandomItemTask(e.getPlayer());
            randomItemList.add(e.getPlayer());
            randomTaskMap.put(e.getPlayer(), task);
            task.runTaskTimer(ClassicDupe.getPlugin(), 0, 20*60);
            e.getPlayer().sendMessage(Utils.cmdMsg("&aEvery &e60 &ayou will recieve a random item. Execute /random to disable or enable this"));
            ChatColorCMD.colorProfiles.put(e.getPlayer().getUniqueId().toString(), "&7");
            nakedProtection.put(e.getPlayer(), System.currentTimeMillis());
            e.getPlayer().sendMessage(Utils.cmdMsg("&aYou currently have naked protection on. This means you cannot pvp but you are safe for 10 minutes. To turn this off execute /nakedoff"));
            return;
        }
        PlayerDatabase.PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(e.getPlayer().getUniqueId().toString());
        RandomItemTask task = new RandomItemTask(e.getPlayer());
        randomTaskMap.put(e.getPlayer(), task);
        task.runTaskTimer(ClassicDupe.getPlugin(), 0, 20*60);
        if(playerData.randomitem) {
            randomItemList.add(e.getPlayer());
            e.getPlayer().sendMessage(Utils.cmdMsg("&aEvery &e60 &ayou will recieve a random item. Execute /random to disable or enable this"));
        }
        ChatColorCMD.colorProfiles.put(e.getPlayer().getUniqueId().toString(), playerData.chatcolor);
        if(playerData.gradient) {
            ChatGradientCMD.gradientProfiles.put(e.getPlayer().getUniqueId().toString(), new ChatGradientCMD.GradientProfiles(
                    playerData.gradientfrom,
                    playerData.gradientto
            ));
        }
        e.joinMessage(Component.text(
                Utils.format("&8[&a+&8] " +
                        ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getPrefix() +
                        e.getPlayer().getName())
        ));

    }

    public static class JoinEventTasks extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(p -> p.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    999999999,
                    1
            )));
            for(int i = 0; i < nakedProtection.size(); i++) {
                Player player = nakedProtection.keySet().stream().toList().get(i);
                Long time = nakedProtection.get(player);
                if(time + (10*60*1000) < System.currentTimeMillis()) {
                    nakedProtection.remove(player);
                    if(player.isOnline()) player.sendMessage(Utils.cmdMsg("&cYou are no longer protected by naked protection"));
                }
            }
        }
    }

    public static class RandomItemTask extends BukkitRunnable {

        private final Player player;

        public RandomItemTask(Player pl) {
            player = pl;
        }

        @Override
        public void run() {
            if(!randomItemList.contains(player)) return;
            Material random = Material.values()[new Random().nextInt(Material.values().length)];
            player.getInventory().addItem(new ItemStack(random));
        }
    }

}
