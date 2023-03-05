package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.util.ArrayList;
import java.util.List;

public class StatsCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("&cPlease include a player to get the stats of"));
            return true;
        }
        OfflinePlayer tarj = Bukkit.getOfflinePlayer(args[0]);
        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(tarj.getUniqueId().toString());
        if(stats == null) {
            sender.sendMessage(Utils.cmdMsg("&cThat player has not joined the server before"));
            return true;
        }
        sender.sendMessage(Utils.cmdMsg("&aStats of &e" + tarj.getName()));
        sender.sendMessage(Utils.format("&7- &aKills: &e" + stats.kills));
        sender.sendMessage(Utils.format("&7- &aDeaths: &e" + stats.deaths));
        sender.sendMessage(Utils.format("&7- &aKDR: &e" + stats.kdr));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return Utils.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
