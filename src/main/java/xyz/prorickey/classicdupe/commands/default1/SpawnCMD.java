package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class SpawnCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String name;
        if(command.getName().equals("spawn")) name = "hub";
        else name = command.getName();
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            }
            if(Combat.inCombat.containsKey(player)) {
                player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command in combat"));
                return true;
            }
            player.teleport(ClassicDupe.getDatabase().getSpawn(name));
            Utils.cmdMsg("<green>Teleported you to " + command.getName());
        } else {
            if(!sender.hasPermission("default." + command.getName() + ".others")) return true;
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>That player is not currently on the server"));
                return true;
            }
            p.teleport(ClassicDupe.getDatabase().getSpawn(name));
            Utils.cmdMsg("<green>You were sent to spawn by <yellow>" + sender.getName());
            Utils.cmdMsg("<green>Sent <yellow>" + p.getName() + "<green> to " + command.getName());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1 && sender.hasPermission("default.spawn.others")) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
