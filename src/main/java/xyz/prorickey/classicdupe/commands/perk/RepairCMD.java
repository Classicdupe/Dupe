package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import xyz.prorickey.classicdupe.Utils;

public class RepairCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        p.getInventory().forEach(item -> {
            if(item != null) {
                ItemMeta meta = item.getItemMeta();
                if(meta instanceof Damageable dmeta) dmeta.setDamage(0);
                item.setItemMeta(meta);
            }
        });
        p.sendMessage(Utils.cmdMsg("<green>Repaired all items in your inventory"));
        return true;
    }
    
}
