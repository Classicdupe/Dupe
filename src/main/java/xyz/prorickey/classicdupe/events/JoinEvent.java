package xyz.prorickey.classicdupe.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.discord.LinkRewards;

import java.util.*;

public class JoinEvent implements Listener {

    public static final Map<Player, RandomItemTask> randomTaskMap = new HashMap<>();
    public static final List<Player> randomItemList = new ArrayList<>();
    public static final Map<Player, Long> nakedProtection = new HashMap<>();
    public static final List<Player> nightVision = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ClassicDupe.getClanDatabase().updateClanMemberInfo(e.getPlayer());
        if(ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId()) == null) {
            ClassicDupe.getDatabase().getPlayerDatabase().playerDataUpdateAndLoad(e.getPlayer());
            if(ClassicDupe.getDatabase().getSpawn("hub") != null) e.getPlayer().teleport(ClassicDupe.getDatabase().getSpawn("hub"));
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
        ClassicDupe.getDatabase().getPlayerDatabase().playerDataUpdateAndLoad(e.getPlayer());
        ClassicDupe.getDatabase().getHomesDatabase().loadPlayer(e.getPlayer());
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(playerData.chatcolor.startsWith("&")) playerData.setChatColor(Utils.convertColorCodesToAdventure(playerData.chatcolor));
        if(playerData.nickname != null && Utils.convertColorCodesToAdventure(playerData.nickname).length() != playerData.nickname.length())
            playerData.setNickname(Utils.convertColorCodesToAdventure(playerData.nickname));
        if(playerData.night) nightVision.add(e.getPlayer());
        RandomItemTask task = new RandomItemTask(e.getPlayer());
        randomTaskMap.put(e.getPlayer(), task);
        task.runTaskTimer(ClassicDupe.getPlugin(), 0, 20*60);
        if(playerData.randomitem) {
            randomItemList.add(e.getPlayer());
            e.getPlayer().sendMessage(Utils.cmdMsg("<green>Every <yellow>60 <green>you will recieve a random item. Execute /random to disable or enable this"));
        }
        if(playerData.chatcolor.startsWith("&")) {
            playerData.setChatColor(Utils.convertColorCodesToAdventure(playerData.chatcolor));
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

        removeOpal(e.getPlayer().getInventory());
        removeOpal(e.getPlayer().getEnderChest());
    }

    public static void removeOpal(Inventory inventory) {
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null) continue;
            if(DupeCMD.shulkerBoxes.contains(itemStack.getType())) removeOpal(((ShulkerBox) ((BlockStateMeta) itemStack.getItemMeta()).getBlockState()).getInventory());
            if(itemStack.getItemMeta() instanceof BundleMeta) {
                BundleMeta meta = ((BundleMeta) itemStack.getItemMeta());
                meta.getItems().forEach(item -> {
                    ItemMeta itemMeta2 = item.getItemMeta();
                    if(itemMeta2 != null) {
                        if(itemMeta2.getPersistentDataContainer().has(new NamespacedKey(ClassicDupe.getPlugin(), "opal"))) {
                            meta.getItems().remove(item);
                        }
                    }
                });
                itemStack.setItemMeta(meta);
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) continue;
            if(itemMeta.getPersistentDataContainer().has(new NamespacedKey(ClassicDupe.getPlugin(), "opal"))) {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    public static final Map<Player, Long> afkTime = new HashMap<>();
    public static final Integer AFK_TIME_NEEDED = 5*60*1000;

    public static class JoinEventTasks extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers()
                    .stream().filter(nightVision::contains)
                    .forEach(p -> p.addPotionEffect(new PotionEffect(
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
            if(Config.getConfig().getBoolean("dev")) Bukkit.getOnlinePlayers().forEach(player -> {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
                ProtectedRegion region = regions.getRegion("afk");
                if(region != null && region.contains(BukkitAdapter.asBlockVector(player.getLocation()))) {
                    if(!afkTime.containsKey(player)) afkTime.put(player, System.currentTimeMillis()+AFK_TIME_NEEDED);
                    else if(afkTime.get(player) < System.currentTimeMillis()) {
                        afkTime.remove(player);
                        ClassicDupe.getDatabase()
                                .getPlayerDatabase()
                                .getPlayerData(player.getUniqueId()).addBalance(1);
                    }
                } else if(afkTime.containsKey(player)) afkTime.remove(player);
            });
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
