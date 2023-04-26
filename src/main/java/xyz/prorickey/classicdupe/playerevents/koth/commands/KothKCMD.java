package xyz.prorickey.classicdupe.playerevents.koth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class KothKCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Utils.format("<yellow><b>KOTH Help"));
            sender.sendMessage(Utils.format("<yellow>/koth start"));
            sender.sendMessage(Utils.format("<yellow>/koth stop"));
            sender.sendMessage(Utils.format("<yellow>/koth cancel"));
            sender.sendMessage(Utils.format("<yellow>/koth status"));
            return true;
        }
        if(args[0].equalsIgnoreCase("start")) {
            if(KOTHEventManager.running) {
                sender.sendMessage(Utils.cmdMsg("<red>A KOTH event is already running"));
                return true;
            }
            KOTHEventManager.startKOTH();
        } else if(args[0].equalsIgnoreCase("stop")) {
            if(!KOTHEventManager.running) {
                sender.sendMessage(Utils.cmdMsg("<red>A KOTH event is not running"));
                return true;
            }
            KOTHEventManager.endKOTHEvent();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("start", "stop"));
        return new ArrayList<>();
    }
}
