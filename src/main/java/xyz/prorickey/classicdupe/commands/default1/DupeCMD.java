package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.customitems.CIKeys;
import xyz.prorickey.classicdupe.events.Combat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DupeCMD implements CommandExecutor, TabCompleter {

    public static final List<Material> forbiddenDupes = new ArrayList<>();
    public static final List<Material> forbiddenDupesInCombat = new ArrayList<>();
    public static final NamespacedKey undupableKey = new NamespacedKey(ClassicDupe.getPlugin(), "undupeable");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int dupeNum = 1;
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(!checkDupable(p.getInventory().getItemInMainHand())) {
            p.sendMessage(Utils.cmdMsg("<red>That item is undupable"));
            return true;
        }
        if(Combat.inCombat.containsKey(p.getPlayer()) && forbiddenDupesInCombat.contains(p.getInventory().getItemInMainHand().getType())) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot dupe that item while in combat"));
            return true;
        }
        if(Combat.inCombat.containsKey(p.getPlayer()) &&
                p.getInventory().getItemInMainHand().getItemMeta() instanceof PotionMeta potionMeta &&
                    (potionMeta.getBasePotionData().getType().equals(PotionType.INSTANT_HEAL) ||
                    potionMeta.getBasePotionData().getType().equals(PotionType.REGEN))) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot dupe that item while in combat"));
            return true;
        }
        if(args.length > 0) {
            try {
                dupeNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
            if(dupeNum < 1) dupeNum = 1;
            if(dupeNum > 6) dupeNum = 6;
        }
        for(int i = 0; i < dupeNum; i++) p.getInventory().addItem(p.getInventory().getItemInMainHand());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of("1", "2", "3", "4", "5", "6");
        }
        return new ArrayList<>();
    }

    public static final List<Material> shulkerBoxes = List.of(
        Material.SHULKER_BOX,
        Material.WHITE_SHULKER_BOX,
        Material.ORANGE_SHULKER_BOX,
        Material.MAGENTA_SHULKER_BOX,
        Material.LIGHT_BLUE_SHULKER_BOX,
        Material.YELLOW_SHULKER_BOX,
        Material.LIME_SHULKER_BOX,
        Material.PINK_SHULKER_BOX,
        Material.GRAY_SHULKER_BOX,
        Material.LIGHT_GRAY_SHULKER_BOX,
        Material.CYAN_SHULKER_BOX,
        Material.PURPLE_SHULKER_BOX,
        Material.BLUE_SHULKER_BOX,
        Material.BROWN_SHULKER_BOX,
        Material.GREEN_SHULKER_BOX,
        Material.RED_SHULKER_BOX,
        Material.BLACK_SHULKER_BOX
    );

    public static Boolean checkDupable(ItemStack item) {
        if(item == null || item.getType() == Material.AIR) return true;
        if(forbiddenDupes.contains(item.getType())) return false;
        if(Boolean.TRUE.equals(
                Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(undupableKey, PersistentDataType.BOOLEAN)) ||
                item.getItemMeta().getPersistentDataContainer().has(CIKeys.FBWAND, PersistentDataType.STRING) ||
                item.getItemMeta().getPersistentDataContainer().has(CIKeys.BURSTBOW, PersistentDataType.STRING) ||
                item.getItemMeta().getPersistentDataContainer().has(CIKeys.PVPPOT, PersistentDataType.STRING) ||
                item.getItemMeta().getPersistentDataContainer().has(CIKeys.PVPPOT2, PersistentDataType.STRING)
        )) return false;
        if(shulkerBoxes.contains(item.getType())) {
            AtomicBoolean illegal = new AtomicBoolean(false);
            ShulkerBox box = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();
            box.getInventory().forEach(itemStack -> {
                if(!checkDupable(itemStack)) illegal.set(true);
            });
            return !illegal.get();
        }
        if(item.getType().equals(Material.BUNDLE)) {
            AtomicBoolean illegal = new AtomicBoolean(false);
            BundleMeta bundle = (BundleMeta) item.getItemMeta();
            bundle.getItems().forEach(itemStack -> {
                if(!checkDupable(itemStack)) illegal.set(true);
            });
            return !illegal.get();
        }
        return !(item.getItemMeta() instanceof ArmorMeta armorMeta) || !armorMeta.hasTrim();
    }

}