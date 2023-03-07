package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.Combat;

import java.util.ArrayList;
import java.util.List;

public class SpawnCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
                return true;
            }
            if(Combat.inCombat.containsKey(player)) {
                player.sendMessage(Utils.cmdMsg("&cYou cannot execute this command in combat"));
                return true;
            }
            player.teleport(ClassicDupe.getDatabase().spawn);
            Utils.cmdMsg("&aTeleported you to spawn");
        } else {
            if(!sender.hasPermission("default.spawn.others")) return true;
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("&cThat player is not currently on the server"));
                return true;
            }
            p.teleport(ClassicDupe.getDatabase().spawn);
            Utils.cmdMsg("&aYou were sent to spawn by &e" + sender.getName());
            Utils.cmdMsg("&aSent &e" + p.getName() + "&a to spawn");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1 && sender.hasPermission("default.spawn.others")) return Utils.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
