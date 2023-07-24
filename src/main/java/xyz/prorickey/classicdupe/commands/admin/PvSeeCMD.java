package xyz.prorickey.classicdupe.commands.admin;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.custom.CustomInvData;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class PvSeeCMD implements CommandExecutor, TabCompleter, Listener {

    public static final Map<String, Inventory> vaultGuis = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute that command from console"));
            return true;
        }

        if (args.length == 0 ) {
            sender.sendMessage(Utils.cmdMsg("<red>You must specify a PV number & player"));
        }

        if (args.length == 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You must specify a player"));
        }
        Player senderPlayer = (Player) sender;
        Player target = (Player) ClassicDupe.getPlugin().getServer().getOfflinePlayer(args[1]);
        if (target.hasPlayedBefore()) {
            Map<Integer, ItemStack> vaultMap = ClassicDupe.getPVDatabase().getVault(target.getUniqueId().toString(), Integer.parseInt(args[0]));
            if(vaultMap == null) {
                sender.sendMessage(Utils.cmdMsg("<red>That player doesn't have a vault under that ID!"));
                return true;
            }
            sender.sendMessage(Utils.cmdMsg("<green>Opening vault #" + args[0] + " for " + args[1]));

            Inventory vaultGUI = Bukkit.createInventory(new CustomInvData(target.getUniqueId().toString(), Integer.parseInt(args[0])), 54, Utils.cmdMsg("<green>Opening vault #" + args[0] + " for " + args[1]));
            vaultMap.forEach(vaultGUI::setItem);
            senderPlayer.openInventory(vaultGUI);
            vaultGuis.put(target.getUniqueId().toString(), vaultGUI);
            return true;
        }
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("1", "2"));
        if(args.length == 2) return TabComplete.tabCompletionsSearch(args[1], ClassicDupe.getOnlinePlayerUsernames().stream().toList());
        return new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        if (!(e.getInventory().getHolder() instanceof CustomInvData)) return;
        if(!e.getInventory().equals(vaultGuis.get(e.getPlayer().getUniqueId().toString()))) return;

        vaultGuis.remove(((CustomInvData) e.getInventory().getHolder()).uuid);
        String name = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        int vault = ((CustomInvData) e.getInventory().getHolder()).vault;
        ClassicDupe.getPVDatabase().setVault(e.getPlayer().getUniqueId().toString(), vault, e.getInventory());
        e.getPlayer().sendMessage(Utils.cmdMsg("<green>Saved vault #" + vault + " for " + name));
    }
}
