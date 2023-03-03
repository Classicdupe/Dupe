package xyz.prorickey.classicdupe.commands.perk;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerVaultCMD implements CommandExecutor, TabCompleter, Listener {

    public static Map<String, Inventory> vaultGuis = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.format("&cYou cannot execute that command from console."));
            return true;
        }
        if(args.length == 0) {
            p.sendMessage(Utils.format("&cYou must include which vault you would like to open"));
            return true;
        }
        int vault = Integer.parseInt(args[0]);
        Map<Integer, ItemStack> vaultMap = ClassicDupe.getPVDatabase().getVault(p.getUniqueId().toString(), vault);
        if(vaultMap == null) {
            p.sendMessage(Utils.format("&cYou do not have access to that vault."));
            return true;
        }
        Inventory vaultGUI = Bukkit.createInventory(null, 54, Utils.format("&9&lVault #" + vault));
        vaultMap.forEach((key, item) -> vaultGUI.setItem(key, item));
        p.openInventory(vaultGUI);
        vaultGuis.put(p.getUniqueId().toString(), vaultGUI);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getInventory().equals(vaultGuis.get(e.getWhoClicked().getUniqueId().toString()))) return;
        int slot = e.getRawSlot();
        String name = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        int vault = Integer.parseInt(name.substring(name.length() - 1));
        ItemStack changedTo = e.getCurrentItem();
        ClassicDupe.getPVDatabase().setItemInVault(e.getWhoClicked().getUniqueId().toString(), vault, slot, changedTo);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!e.getInventory().equals(vaultGuis.get(e.getPlayer().getUniqueId().toString()))) return;
        vaultGuis.remove(e.getPlayer().getUniqueId().toString());
    }
}
