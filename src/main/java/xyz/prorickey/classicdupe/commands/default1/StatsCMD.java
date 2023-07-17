package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.metrics.Metrics;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class StatsCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(player.getUniqueId().toString());
            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
            player.sendMessage(Utils.cmdMsg("<green>Stats of <yellow>" + player.getName()));
            player.sendMessage(Utils.format("<gray>- <green>Kills: <yellow>" + stats.kills));
            player.sendMessage(Utils.format("<gray>- <green>Deaths: <yellow>" + stats.deaths));
            player.sendMessage(Utils.format("<gray>- <green>KDR: <yellow>" + stats.kdr));
            player.sendMessage(Utils.format("<gray>- <green>Balance: <yellow>" + data.getBalance()));
            player.sendMessage(Utils.format("<gray>- <green>Playtime: <yellow>" + Metrics.getPlayerMetrics().getPlaytimeFormatted(player.getUniqueId())));
            return true;
        } else if(args.length == 1) {
            OfflinePlayer tarj = Bukkit.getOfflinePlayer(args[0]);
            PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(tarj.getUniqueId().toString());
            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(tarj.getUniqueId());
            if(stats == null || data == null) {
                player.sendMessage(Utils.cmdMsg("<red>That player has not joined the server before"));
                return true;
            }
            player.sendMessage(Utils.cmdMsg("<green>Stats of <yellow>" + tarj.getName()));
            player.sendMessage(Utils.format("<gray>- <green>Kills: <yellow>" + stats.kills));
            player.sendMessage(Utils.format("<gray>- <green>Deaths: <yellow>" + stats.deaths));
            player.sendMessage(Utils.format("<gray>- <green>KDR: <yellow>" + stats.kdr));
            player.sendMessage(Utils.format("<gray>- <green>Balance: <yellow>" + data.getBalance()));
            player.sendMessage(Utils.format("<gray>- <green>Playtime: <yellow>" + Metrics.getPlayerMetrics().getPlaytimeFormatted(tarj.getUniqueId())));
            return true;
        }
        if(!player.hasPermission("admin.editStats")) {
            player.sendMessage(Utils.format("<red>You do not have permission to edit player stats"));
            return true;
        }
        OfflinePlayer tarj = Bukkit.getOfflinePlayer(args[0]);
        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(tarj.getUniqueId().toString());
        if(stats == null) {
            player.sendMessage(Utils.cmdMsg("<red>That player has not joined the server before"));
            return true;
        }
        switch(args[1].toLowerCase()) {
            case "setkills" -> {
                if(args.length == 3) {
                    int kills;
                    try {
                        kills = Integer.parseInt(args[2]);
                    } catch(NumberFormatException e) {
                        player.sendMessage(Utils.format("<red>You must provide a number to set the kills to"));
                        return true;
                    }
                    ClassicDupe.getDatabase().getPlayerDatabase().setKills(tarj.getUniqueId(), kills);
                    player.sendMessage(Utils.format("<green>Set <yellow>" + tarj.getName() + "'s <green> kills to <yellow>" + kills));
                } else {
                    ClassicDupe.getDatabase().getPlayerDatabase().setKills(tarj.getUniqueId(), 0);
                    player.sendMessage(Utils.format("<green>Set <yellow>" + tarj.getName() + "'s <green> kills to <yellow>0"));
                }
            }
            case "setdeaths" -> {
                if(args.length == 3) {
                    int deaths;
                    try {
                        deaths = Integer.parseInt(args[2]);
                    } catch(NumberFormatException e) {
                        player.sendMessage(Utils.format("<red>You must provide a number to set the kills to"));
                        return true;
                    }
                    ClassicDupe.getDatabase().getPlayerDatabase().setDeaths(tarj.getUniqueId(), deaths);
                    player.sendMessage(Utils.format("<green>Set <yellow>" + tarj.getName() + "'s <green> deaths to <yellow>" + deaths));
                } else {
                    ClassicDupe.getDatabase().getPlayerDatabase().setDeaths(tarj.getUniqueId(), 0);
                    player.sendMessage(Utils.format("<green>Set <yellow>" + tarj.getName() + "'s <green> deaths to <yellow>0"));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        if(args.length == 2 && sender.hasPermission("admin.editStats")) return TabComplete.tabCompletionsSearch(args[1], List.of("setkills", "setdeaths"));
        return new ArrayList<>();
    }
}
