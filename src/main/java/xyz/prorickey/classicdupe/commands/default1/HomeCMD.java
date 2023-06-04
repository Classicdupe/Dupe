package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.Combat;

import java.util.ArrayList;
import java.util.List;

public class HomeCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(Combat.inCombat.containsKey(player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot teleport while in combat"));
            return true;
        }
        if(args.length == 0) {
            Location loc = ClassicDupe.getDatabase().getHomesDatabase().getHome(player, "default");
            if(loc == null) {
                sender.sendMessage(Utils.cmdMsg("<red>You do not have a default home set"));
                return true;
            }
            player.teleport(loc);
            player.sendMessage(Utils.cmdMsg("<green>Teleported to your default home"));
        } else {
            Location loc = ClassicDupe.getDatabase().getHomesDatabase().getHome(player, args[0]);
            if(loc == null) {
                sender.sendMessage(Utils.cmdMsg("<red>The home <yellow>" + args[0] + " <red>does not exist"));
                return true;
            }
            player.teleport(loc);
            player.sendMessage(Utils.cmdMsg("<green>Teleported to <yellow>" + args[0] + "<green> home"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1 && sender instanceof Player player) { return ClassicDupe.getDatabase().getHomesDatabase().getHomes(player.getUniqueId()).keySet().stream().toList(); }
        return new ArrayList<>();
    }

}
