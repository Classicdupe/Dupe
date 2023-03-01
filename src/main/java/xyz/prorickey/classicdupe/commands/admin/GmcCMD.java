package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class GmcCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.format("&cYou cannot change console's gamemode."));
                return true;
            }
            Player p = (Player) sender;
            if(!p.hasPermission("admin.gamemode.creative")) {
                sender.sendMessage("&cYou do not have permission to change your gamemode to creative mode.");
                return true;
            }
            if(p.getGameMode() == GameMode.CREATIVE) {
                sender.sendMessage(Utils.format("&cYou're already in creative mode."));
                return true;
            }
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage(Utils.format("&eSet your gamemode to &acreative &emode."));
            return true;
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.format("&c" + args[0] + " is not currently online."));
                return true;
            }
            if(!sender.hasPermission("admin.gamemode.creative.others") && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Utils.format("&cYou do not have permission to set other player's gamemode to creative."));
                return true;
            }
            if(p.getGameMode() == GameMode.CREATIVE) {
                sender.sendMessage(Utils.format("&6" + p.getName() + "'s &cgamemode is already creative mode."));
                return true;
            }
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage(Utils.format("&eYour gamemode has been set to &acreative&e mode by &6" + sender.getName()));
            sender.sendMessage(Utils.format("&eSet " + p.getName() + "'s gamemode to &acreative"));
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            return Utils.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        }
        return new ArrayList<>();
    }

}
