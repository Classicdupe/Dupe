package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class SudoCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You need to provide a player to sudo"));
            return true;
        } else if(args.length == 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You need to provide a message or command for the player to execute"));
            return true;
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>That player is not currently on the server"));
                return true;
            }
            StringBuilder msg = new StringBuilder();
            for(int i = 1; i < args.length; i++) { msg.append(args[i]).append(" "); }
            String message = msg.toString();
            if(message.startsWith("/")) {
                p.chat(message);
                sender.sendMessage(Utils.cmdMsg("<green>Made <yellow>" + p.getName() + "<green> execute the command <yellow>" + message));
            } else {
                p.chat(message);
                sender.sendMessage(Utils.cmdMsg("<green>Made <yellow>" + p.getName() + "<green> send the message <yellow>" + message));
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
