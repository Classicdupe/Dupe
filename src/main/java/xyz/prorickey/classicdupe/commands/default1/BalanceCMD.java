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
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BalanceCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            }
            PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
            player.sendMessage(Utils.cmdMsg("<green>You have <yellow>" + playerData.getBalance() + " <green>dabloons"));
            return true;
        }
        switch(args[0].toLowerCase()) {
            case "top" -> {
                Map<Integer, PlayerData> top = ClassicDupe.getDatabase().getPlayerDatabase().balanceTop;
                sender.sendMessage(Utils.format("<green>Top 10 balances:"));
                for(int i = 1; i <= 10; i++) {
                    if(top.get(i) == null) break;
                    sender.sendMessage(Utils.format("  <green>" + i + ". <yellow>" + top.get(i).getName() + " - <yellow>" + top.get(i).getBalance() + " <green>dabloons"));
                }
            }
            case "set" -> {
                if(!sender.hasPermission("classicdupe.balance.set")) {
                    sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to execute this command"));
                    return true;
                }
                if(args.length < 3) {
                    sender.sendMessage(Utils.cmdMsg("<red>Usage: /balance set <player> <amount>"));
                    return true;
                }
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[1]);
                if(offPlayer == null) {
                    sender.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                    return true;
                }
                PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(offPlayer.getUniqueId());
                if(playerData == null) {
                    sender.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                    return true;
                }
                if(!args[2].matches("^[0-9]+$")) {
                    sender.sendMessage(Utils.cmdMsg("<red>That is not a valid number"));
                    return true;
                }
                playerData.setBalance(Integer.parseInt(args[2]));
                sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + playerData.getName() + "'s <green>balance to <yellow>" + args[2] + " <green>dabloons"));
            }
            case "add" -> {
                if(!sender.hasPermission("classicdupe.balance.add")) {
                    sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to execute this command"));
                    return true;
                }
                if(args.length < 3) {
                    sender.sendMessage(Utils.cmdMsg("<red>Usage: /balance add <player> <amount>"));
                    return true;
                }
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[1]);
                if(offPlayer == null) {
                    sender.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                    return true;
                }
                PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(offPlayer.getUniqueId());
                if(playerData == null) {
                    sender.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                    return true;
                }
                if(!args[2].matches("^[0-9]+$")) {
                    sender.sendMessage(Utils.cmdMsg("<red>That is not a valid number"));
                    return true;
                }
                playerData.setBalance(playerData.getBalance() + Integer.parseInt(args[2]));
                sender.sendMessage(Utils.cmdMsg("<green>Added <yellow>" + args[2] + " <green>dabloons to <yellow>" + playerData.getName() + "'s <green>balance"));
            }
            case "remove" -> {
                if(!sender.hasPermission("classicdupe.balance.remove")) {
                    sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to execute this command"));
                    return true;
                }
                if(args.length < 3) {
                    sender.sendMessage(Utils.cmdMsg("<red>Usage: /balance remove <player> <amount>"));
                    return true;
                }
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[1]);
                if(offPlayer == null) {
                    sender.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                    return true;
                }
                PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(offPlayer.getUniqueId());
                if(playerData == null) {
                    sender.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                    return true;
                }
                if(!args[2].matches("^[0-9]+$")) {
                    sender.sendMessage(Utils.cmdMsg("<red>That is not a valid number"));
                    return true;
                }
                playerData.setBalance(playerData.getBalance() - Integer.parseInt(args[2]));
                sender.sendMessage(Utils.cmdMsg("<green>Removed <yellow>" + args[2] + " <green>dabloons from <yellow>" + playerData.getName() + "'s <green>balance"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> tabCompletions = new ArrayList<>(List.of("top"));
            if(sender.hasPermission("admin.editbalance")) tabCompletions.addAll(List.of("set", "add", "remove"));
            return TabComplete.tabCompletionsSearch(args[0], tabCompletions);
        } else if(args.length == 2) return TabComplete.tabCompletionsSearch(args[1], ClassicDupe.getOnlinePlayerUsernames());
        else return new ArrayList<>();
    }

}
