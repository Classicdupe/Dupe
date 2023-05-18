package xyz.prorickey.classicdupe.commands.moderator;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.Database;

import java.util.List;

public class BlockCommandCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if(args.length == 0) {
            p.sendMessage(Utils.cmdMsg("<red>Who would you block from using teleport commands?"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null || !target.isOnline()) {
            p.sendMessage(Utils.cmdMsg("<red>That player is not currently online"));
            return true;
        }
        if (Database.blockedToUseCommands.contains(target) && target != p){
            Database.blockedToUseCommands.remove(target);
            p.sendMessage(Utils.format("<green>\"" + target.getName() + "\" is not blocked to use commands anymore."));
        }
        else if (!Database.blockedToUseCommands.contains(target) && target != p){
            Database.blockedToUseCommands.add(target);
            p.sendMessage(Utils.format("<red>\"" + target.getName() + "\" is blocked to use commands."));
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
