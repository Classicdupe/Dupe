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

import static org.bukkit.GameMode.*;

public class GamemodeCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            } else {
                switch (((Player) sender).getGameMode()) {
                    case CREATIVE -> sender.sendMessage(Utils.cmdMsg("&aYou are currently in &ecreative &amode"));
                    case SURVIVAL -> sender.sendMessage(Utils.cmdMsg("&aYou are currently in &esurvival &amode"));
                    case SPECTATOR -> sender.sendMessage(Utils.cmdMsg("&aYou are currently in &espectator &amode"));
                    case ADVENTURE -> sender.sendMessage(Utils.cmdMsg("&aYou are currently in &eadventure &amode"));
                }
            }
            return true;
        } else if(args.length < 2) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
                return true;
            } else {
                Player p = (Player) sender;
                switch (args[0]) {
                    case "creative" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == CREATIVE) {
                            sender.sendMessage(Utils.cmdMsg("&cYou're already in creative mode"));
                            return true;
                        }
                        p.setGameMode(CREATIVE);
                        p.sendMessage(Utils.cmdMsg("&aSet your gamemode to &e" + args[0] + " &amode"));
                        return true;
                    }
                    case "survival" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == SURVIVAL) {
                            sender.sendMessage(Utils.cmdMsg("&cYou're already in " + args[0] + " mode"));
                            return true;
                        }
                        p.setGameMode(SURVIVAL);
                        p.sendMessage(Utils.cmdMsg("&aSet your gamemode to &e" + args[0] + " &amode."));
                        return true;
                    }
                    case "adventure" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == GameMode.ADVENTURE) {
                            sender.sendMessage(Utils.cmdMsg("&cYou're already in " + args[0] + " mode"));
                            return true;
                        }
                        p.setGameMode(GameMode.ADVENTURE);
                        p.sendMessage(Utils.cmdMsg("&aSet your gamemode to &e" + args[0] + " &amode"));
                        return true;
                    }
                    case "spectator" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == SPECTATOR) {
                            sender.sendMessage(Utils.cmdMsg("&cYou're already in " + args[0] + " mode"));
                            return true;
                        }
                        p.setGameMode(SPECTATOR);
                        p.sendMessage(Utils.cmdMsg("&aSet your gamemode to &e" + args[0] + " &amode"));
                        return true;
                    }
                    default -> {
                        sender.sendMessage(Utils.cmdMsg("&cThat gamemode does not exist"));
                        return true;
                    }
                }
            }
        } else {
            Player p = Bukkit.getServer().getPlayer(args[1]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("&c" + args[1] + " is not currently online"));
                return true;
            }
            switch (args[0]) {
                case "creative" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == CREATIVE) {
                        sender.sendMessage(Utils.cmdMsg("&e" + p.getName() + "'s &cgamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(CREATIVE);
                    p.sendMessage(Utils.cmdMsg("&aYour gamemode has been set to &e" + args[0] + "&a mode by &e" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("&aSet &e" + p.getName() + "'s &agamemode to &e" + args[0]));
                    return true;
                }
                case "survival" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == SURVIVAL) {
                        sender.sendMessage(Utils.cmdMsg("&e" + p.getName() + "'s &cgamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(SURVIVAL);
                    p.sendMessage(Utils.cmdMsg("&aYour gamemode has been set to &e" + args[0] + "&a mode by &e" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("&aSet &e" + p.getName() + "'s &agamemode to &e" + args[0]));
                    return true;
                }
                case "adventure" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == GameMode.ADVENTURE) {
                        sender.sendMessage(Utils.cmdMsg("&e" + p.getName() + "'s &cgamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(GameMode.ADVENTURE);
                    p.sendMessage(Utils.cmdMsg("&aYour gamemode has been set to &e" + args[0] + "&a mode by &e" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("&aSet &e" + p.getName() + "'s &agamemode to &e" + args[0]));
                    return true;
                }
                case "spectator" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("&cYou do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == SPECTATOR) {
                        sender.sendMessage(Utils.cmdMsg("&e" + p.getName() + "'s &cgamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(SPECTATOR);
                    p.sendMessage(Utils.cmdMsg("&aYour gamemode has been set to &e" + args[0] + "&a mode by &e" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("&aSet &e" + p.getName() + "'s &agamemode to &e" + args[0]));
                    return true;
                }
                default -> {
                    sender.sendMessage(Utils.cmdMsg("&cThat gamemode does not exist"));
                    return true;
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) return Utils.tabCompletionsSearch(args[0], List.of("creative", "survival", "spectator", "adventure"));
        else if(args.length == 2) return Utils.tabCompletionsSearch(args[1], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }

}
