package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class SetHomeCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            if(ClassicDupe.getDatabase().getHomesDatabase().getHome(player, "default") == null &&
                ClassicDupe.getDatabase().getHomesDatabase().getHomes(player.getUniqueId()).size() >= Utils.getMaxHomes(player)) {
                player.sendMessage(Utils.cmdMsg("<red>You have reached the maximum amount of homes"));
            } else {
                ClassicDupe.getDatabase().getHomesDatabase().addHome(player, "default", player.getLocation());
                player.sendMessage(Utils.cmdMsg("<green>Set your default home"));
            }
        } else {
            if(ClassicDupe.getDatabase().getHomesDatabase().getHome(player, args[0]) == null &&
                    ClassicDupe.getDatabase().getHomesDatabase().getHomes(player.getUniqueId()).size() >= Utils.getMaxHomes(player)) {
                player.sendMessage(Utils.cmdMsg("<red>You have reached the maximum amount of homes"));
            } else {
                ClassicDupe.getDatabase().getHomesDatabase().addHome(player, args[0], player.getLocation());
                player.sendMessage(Utils.cmdMsg("<green>Set your <yellow>" + args[0] + " <green>home"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
