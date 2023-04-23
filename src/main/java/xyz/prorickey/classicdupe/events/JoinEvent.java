package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.discord.LinkRewards;

import java.util.*;

public class JoinEvent implements Listener {

    public static Map<Player, RandomItemTask> randomTaskMap = new HashMap<>();
    public static List<Player> randomItemList = new ArrayList<>();
    public static Map<Player, Long> nakedProtection = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ClanDatabase.createIfNotExists(e.getPlayer());
        if(ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(e.getPlayer().getUniqueId().toString()) == null) {
            ClassicDupe.getDatabase().getPlayerDatabase().initPlayer(e.getPlayer());
            e.getPlayer().teleport(ClassicDupe.getDatabase().spawn);
            e.joinMessage(Utils.format("<yellow>" + e.getPlayer().getName() + " <green>Just joined for the first time! Give them a warm welcome"));
            e.getPlayer().sendMessage(Utils.cmdMsg("<green>Every <yellow>60 <green>you will recieve a random item. Execute /random to disable or enable this"));
            ChatColorCMD.colorProfiles.put(e.getPlayer().getUniqueId().toString(), "<gray>");
            nakedProtection.put(e.getPlayer(), System.currentTimeMillis());

            // Starting Gear
            e.getPlayer().getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            e.getPlayer().getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            e.getPlayer().getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            e.getPlayer().getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            e.getPlayer().getInventory().addItem(
                    new ItemStack(Material.IRON_SWORD),
                    new ItemStack(Material.IRON_PICKAXE),
                    new ItemStack(Material.IRON_AXE),
                    new ItemStack(Material.COOKED_BEEF, 16)
            );

            RandomItemTask task = new RandomItemTask(e.getPlayer());
            randomItemList.add(e.getPlayer());
            randomTaskMap.put(e.getPlayer(), task);
            task.runTaskTimer(ClassicDupe.getPlugin(), 0, 20*60);

            e.getPlayer().sendMessage(Utils.cmdMsg("<green>You currently have naked protection on. This means you cannot pvp but you are safe for 10 minutes. To turn this off execute /nakedoff"));
            return;
        }
        PlayerDatabase.PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(e.getPlayer().getUniqueId().toString());
        if(playerData.chatcolor.startsWith("&")) ClassicDupe.getDatabase().getPlayerDatabase().setChatColor(e.getPlayer().getUniqueId().toString(),
                    Utils.convertColorCodesToAdventure(playerData.chatcolor));
        if(playerData.nickname != null && Utils.convertColorCodesToAdventure(playerData.nickname).length() != playerData.nickname.length()) ClassicDupe.getDatabase().getPlayerDatabase().setNickname(e.getPlayer().getUniqueId().toString(),
                    Utils.convertColorCodesToAdventure(playerData.nickname));
        RandomItemTask task = new RandomItemTask(e.getPlayer());
        randomTaskMap.put(e.getPlayer(), task);
        task.runTaskTimer(ClassicDupe.getPlugin(), 0, 20*60);
        if(playerData.randomitem) {
            randomItemList.add(e.getPlayer());
            e.getPlayer().sendMessage(Utils.cmdMsg("<green>Every <yellow>60 <green>you will recieve a random item. Execute /random to disable or enable this"));
        }
        if(playerData.chatcolor.startsWith("&")) {
            ClassicDupe.getDatabase().getPlayerDatabase().setChatColor(e.getPlayer().getUniqueId().toString(),
                    Utils.convertColorCodesToAdventure(playerData.chatcolor));
            ChatColorCMD.colorProfiles.put(e.getPlayer().getUniqueId().toString(), Utils.convertColorCodesToAdventure(playerData.chatcolor));
        } else ChatColorCMD.colorProfiles.put(e.getPlayer().getUniqueId().toString(), playerData.chatcolor);
        if(playerData.gradient) {
            ChatGradientCMD.gradientProfiles.put(e.getPlayer().getUniqueId().toString(), new ChatGradientCMD.GradientProfiles(
                    playerData.gradientfrom,
                    playerData.gradientto
            ));
        }
        LinkRewards.checkRewardsForLinking(e.getPlayer());
        LinkRewards.checkRewardsForBoosting(e.getPlayer());
        e.joinMessage(Utils.format("<dark_gray>[<green>+<dark_gray>] ")
                .append(MiniMessage.miniMessage().deserialize(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName())));

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
                    if(player.isOnline()) player.sendMessage(Utils.cmdMsg("<red>You are no longer protected by naked protection"));
                }
            }
        }
    }

    public static class RandomItemTask extends BukkitRunnable {

        private final Player player;

        public RandomItemTask(Player pl) { player = pl; }

        @Override
        public void run() {
            if(!randomItemList.contains(player)) return;
            ItemStack random = ClassicDupe.randomItems.get(new Random().nextInt(ClassicDupe.randomItems.size()));
            player.getInventory().addItem(random);
        }
    }

}
