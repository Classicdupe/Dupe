package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import xyz.prorickey.classicdupe.Utils;

public class RepairCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return true;
        }
        p.getInventory().getItemInMainHand().editMeta(meta -> {
            if(!(meta instanceof Damageable dmeta)) p.sendMessage(Utils.cmdMsg("&cYou cannot repair that item"));
            else {
                dmeta.setDamage(0);
                p.sendMessage(Utils.cmdMsg("&aRepaired item in main hand"));
            }
        });
        return true;
    }
    
}
