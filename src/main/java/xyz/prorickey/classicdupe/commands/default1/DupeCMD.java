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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
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
        if(p.getInventory().getItemInMainHand().getType().equals(Material.SHULKER_BOX)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ShulkerBox box = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();
            AtomicBoolean illegal = new AtomicBoolean(false);
            box.getInventory().forEach(itemStack -> {
                if(!checkDupable(itemStack)) illegal.set(true);
            });
            if(illegal.get()) {
                p.sendMessage(Utils.cmdMsg("<red>You cannot dupe a shulker that contains undupeable items"));
                return true;
            }
        }
        if(p.getInventory().getItemInMainHand().getType().equals(Material.BUNDLE)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            BundleMeta bundle = (BundleMeta) item.getItemMeta();
            AtomicBoolean illegal = new AtomicBoolean(false);
            bundle.getItems().forEach(itemStack -> {
                if(!checkDupable(itemStack)) illegal.set(true);
            });
            if(illegal.get()) {
                p.sendMessage(Utils.cmdMsg("<red>You cannot dupe a bundle that contains undupeable items"));
                return true;
            }
        }
        if(Combat.inCombat.containsKey(p.getPlayer()) && forbiddenDupesInCombat.contains(p.getInventory().getItemInMainHand().getType())) {
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

    public static Boolean checkDupable(ItemStack item) {
        if(forbiddenDupes.contains(item.getType())) return false;
        if(Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(undupableKey, PersistentDataType.BOOLEAN))) return false;
        return !(item.getItemMeta() instanceof ArmorMeta armorMeta) || !armorMeta.hasTrim();
    }

}
