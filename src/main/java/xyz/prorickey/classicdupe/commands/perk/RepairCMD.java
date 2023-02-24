package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import xyz.prorickey.classicdupe.Utils;

public class RepairCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Component.text(Utils.format("&cYou cannot execute this command from console")));
            return true;
        }
        p.getInventory().getItemInMainHand().editMeta(meta -> {
            if(!(meta instanceof Damageable dmeta)) p.sendMessage(Utils.format("&cYou cannot repair that item"));
            else {
                dmeta.setDamage(0);
                p.sendMessage(Utils.format("&aRepaired item in main hand."));
            }
        });
        return true;
    }
    
}
