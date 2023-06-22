package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.custom.armor.OpalArmor;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class CustomArmorCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>Please specify what custom item set you want"));
            return true;
        }
        switch(args[0]) {
            case "opalArmor":
                player.getInventory().addItem(
                        OpalArmor.getOpalBoots(),
                        OpalArmor.getOpalLeggings(),
                        OpalArmor.getOpalChestplate(),
                        OpalArmor.getOpalHelmet()
                );
                player.sendMessage(Utils.cmdMsg("<green>Opal armor set given"));
                return true;
            default:
                player.sendMessage(Utils.cmdMsg("<red>Invalid custom item set"));
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("opalArmor"));
        return new ArrayList<>();
    }

}
