package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class SetSpawnCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player p)) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            }
            ClassicDupe.getDatabase().setSpawn(p.getLocation());
            p.sendMessage(Utils.cmdMsg("<green>Set the spawn location to your location"));
        } else {
            Player tarj = Bukkit.getServer().getPlayer(args[0]);
            if(tarj == null || !tarj.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
                return true;
            }
            ClassicDupe.getDatabase().setSpawn(tarj.getLocation());
            sender.sendMessage(Utils.cmdMsg("<green>Set the spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
