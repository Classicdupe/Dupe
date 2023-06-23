package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.custom.CustomSets;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSetCMD implements CommandExecutor, TabCompleter {

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
        CustomSets set = CustomSets.sets.stream().filter(s -> s.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if(set == null) {
            player.sendMessage(Utils.cmdMsg("<red>That custom item set does not exist"));
            return true;
        } else {
            player.getInventory().addItem(
                    set.getBoots(),
                    set.getLeggings(),
                    set.getChestplate(),
                    set.getHelmet(),
                    set.getSword()
            );
            player.sendMessage(Utils.cmdMsg("<green>You have been given the " + set.getColor() + set.getName() + " <green>custom item set"));
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], CustomSets.sets.stream().map(CustomSets::getName).collect(Collectors.toList()));
        return new ArrayList<>();
    }

}
