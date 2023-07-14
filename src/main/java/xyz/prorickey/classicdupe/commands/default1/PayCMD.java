package xyz.prorickey.classicdupe.commands.default1;

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
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class PayCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length < 2) {
            player.sendMessage(Utils.cmdMsg("<red>You must specify a player's name and an amount to pay them"));
            return true;
        }
        int paying;
        try {
            paying = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            player.sendMessage(Utils.cmdMsg("<red>You must specify a valid number to pay"));
            return true;
        }
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
        if(playerData.getBalance() < paying) {
            player.sendMessage(Utils.cmdMsg("<red>You do not have enough money to pay <yellow>" + args[0]));
            return true;
        }
        PlayerData recpData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
        if(recpData == null) {
            sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>has never joined the server before"));
            return true;
        }
        recpData.addBalance(paying);
        playerData.subtractBalance(paying);
        player.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <green>has been paid <yellow>" + paying + " <green>by you"));
        if(Bukkit.getOfflinePlayer(args[0]).isOnline())
            Bukkit.getOfflinePlayer(args[0]).getPlayer()
                    .sendMessage(Utils.cmdMsg("<green>You have been paid <yellow>" + paying + " <green>by <yellow>" + player.getName()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
