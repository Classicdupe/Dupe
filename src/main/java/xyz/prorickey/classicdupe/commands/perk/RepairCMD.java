package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import xyz.prorickey.classicdupe.Utils;

public class RepairCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Component.text(Utils.format("&cYou cannot execute this command from console")));
            return true;
        }

        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
        if(meta instanceof Damageable) {
            Damageable metad = (Damageable) meta;

            //p.getInventory().getItemInMainHand().setItemMeta(metad);
        }

        return true;
    }
    
}
