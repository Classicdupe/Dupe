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

public class GmsCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("&cYou cannot change console's gamemode"));
                return true;
            }
            Player p = (Player) sender;
            if(!p.hasPermission("admin.gamemode.survival")) {
                sender.sendMessage("&cYou do not have permission to change your gamemode to survival mode");
                return true;
            }
            if(p.getGameMode() == GameMode.SURVIVAL) {
                sender.sendMessage(Utils.cmdMsg("&cYou're already in survival mode"));
                return true;
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(Utils.cmdMsg("&aSet your gamemode to &esurvival &amode"));
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("&c" + args[0] + " is not currently online"));
                return true;
            }
            if(!sender.hasPermission("admin.gamemode.survival.others") && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to set other player's gamemode to survival"));
                return true;
            }
            if(p.getGameMode() == GameMode.SURVIVAL) {
                sender.sendMessage(Utils.cmdMsg("&e" + p.getName() + "'s &cgamemode is already survival mode"));
                return true;
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(Utils.cmdMsg("&aYour gamemode has been set to &esurvival&a mode by &e" + sender.getName()));
            sender.sendMessage(Utils.cmdMsg("&aSet &e" + p.getName() + "'s &agamemode to &esurvival"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) return Utils.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }

}
