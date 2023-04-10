package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.GameMode.*;

public class GamemodeCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            } else {
                switch (((Player) sender).getGameMode()) {
                    case CREATIVE -> sender.sendMessage(Utils.cmdMsg("<green>You are currently in <yellow>creative <green>mode"));
                    case SURVIVAL -> sender.sendMessage(Utils.cmdMsg("<green>You are currently in <yellow>survival <green>mode"));
                    case SPECTATOR -> sender.sendMessage(Utils.cmdMsg("<green>You are currently in <yellow>spectator <green>mode"));
                    case ADVENTURE -> sender.sendMessage(Utils.cmdMsg("<green>You are currently in <yellow>adventure <green>mode"));
                }
            }
            return true;
        } else if(args.length < 2) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            } else {
                Player p = (Player) sender;
                switch (args[0]) {
                    case "creative" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == CREATIVE) {
                            sender.sendMessage(Utils.cmdMsg("<red>You're already in creative mode"));
                            return true;
                        }
                        p.setGameMode(CREATIVE);
                        p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>" + args[0] + " <green>mode"));
                        return true;
                    }
                    case "survival" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == SURVIVAL) {
                            sender.sendMessage(Utils.cmdMsg("<red>You're already in " + args[0] + " mode"));
                            return true;
                        }
                        p.setGameMode(SURVIVAL);
                        p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>" + args[0] + " <green>mode."));
                        return true;
                    }
                    case "adventure" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == GameMode.ADVENTURE) {
                            sender.sendMessage(Utils.cmdMsg("<red>You're already in " + args[0] + " mode"));
                            return true;
                        }
                        p.setGameMode(GameMode.ADVENTURE);
                        p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>" + args[0] + " <green>mode"));
                        return true;
                    }
                    case "spectator" -> {
                        if (!p.hasPermission("admin.gamemode." + args[0])) {
                            sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to change your gamemode to " + args[0] + " mode"));
                            return true;
                        }
                        if (p.getGameMode() == SPECTATOR) {
                            sender.sendMessage(Utils.cmdMsg("<red>You're already in " + args[0] + " mode"));
                            return true;
                        }
                        p.setGameMode(SPECTATOR);
                        p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>" + args[0] + " <green>mode"));
                        return true;
                    }
                    default -> {
                        sender.sendMessage(Utils.cmdMsg("<red>That gamemode does not exist"));
                        return true;
                    }
                }
            }
        } else {
            Player p = Bukkit.getServer().getPlayer(args[1]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>" + args[1] + " is not currently online"));
                return true;
            }
            switch (args[0]) {
                case "creative" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == CREATIVE) {
                        sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(CREATIVE);
                    p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>" + args[0] + "<green> mode by <yellow>" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>" + args[0]));
                    return true;
                }
                case "survival" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == SURVIVAL) {
                        sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(SURVIVAL);
                    p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>" + args[0] + "<green> mode by <yellow>" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>" + args[0]));
                    return true;
                }
                case "adventure" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == GameMode.ADVENTURE) {
                        sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(GameMode.ADVENTURE);
                    p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>" + args[0] + "<green> mode by <yellow>" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>" + args[0]));
                    return true;
                }
                case "spectator" -> {
                    if (!sender.hasPermission("admin.gamemode." + args[0] + ".others") && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to " + args[0]));
                        return true;
                    }
                    if (p.getGameMode() == SPECTATOR) {
                        sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already " + args[0] + " mode"));
                        return true;
                    }
                    p.setGameMode(SPECTATOR);
                    p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>" + args[0] + "<green> mode by <yellow>" + sender.getName()));
                    sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>" + args[0]));
                    return true;
                }
                default -> {
                    sender.sendMessage(Utils.cmdMsg("<red>That gamemode does not exist"));
                    return true;
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("creative", "survival", "spectator", "adventure"));
        else if(args.length == 2) return TabComplete.tabCompletionsSearch(args[1], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }

}
