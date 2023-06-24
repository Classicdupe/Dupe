package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class SetSpawnCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("<red>What spawn would you like to set? <gray>(hub, overworld, nether)"));
            return true;
        } else if(args.length == 1) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "hub" -> {
                    ClassicDupe.getDatabase().setSpawn("hub", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the hub spawn location to your location"));
                    return true;
                }
                case "overworld" -> {
                    ClassicDupe.getDatabase().setSpawn("overworld", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the overworld spawn location to your location"));
                    return true;
                }
                case "nether" -> {
                    ClassicDupe.getDatabase().setSpawn("nether", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the nether spawn location to your location"));
                    return true;
                }
                case "afk" -> {
                    ClassicDupe.getDatabase().setSpawn("afk", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the afk spawn location to your location"));
                    return true;
                }
                default -> {
                    player.sendMessage(Utils.cmdMsg("<red>What spawn would you like to set? <gray>(hub, overworld, nether)"));
                    return true;
                }
            }
        } else {
            Player tarj = Bukkit.getServer().getPlayer(args[0]);
            if(tarj == null || !tarj.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
                return true;
            }
            switch(args[0].toLowerCase()) {
                case "hub" -> {
                    ClassicDupe.getDatabase().setSpawn("hub", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the hub spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                case "overworld" -> {
                    ClassicDupe.getDatabase().setSpawn("overworld", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the overworld spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                case "nether" -> {
                    ClassicDupe.getDatabase().setSpawn("nether", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the nether spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                case "afk" -> {
                    ClassicDupe.getDatabase().setSpawn("afk", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the afk spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                default -> {
                    sender.sendMessage(Utils.cmdMsg("<red>What spawn would you like to set? <gray>(hub, overworld, nether)"));
                    return true;
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("hub", "overworld", "nether"));
            else if(args.length == 2) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
