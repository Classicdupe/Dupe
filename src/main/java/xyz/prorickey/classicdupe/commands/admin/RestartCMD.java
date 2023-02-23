package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class RestartCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length > 0 && args[0].equals("cancel") && ClassicDupe.restartInProgress) {
            ClassicDupe.scheduledRestartCanceled = true;
            ClassicDupe.restartInProgress = false;
            ClassicDupe.rawBroadcast("&aThe server restart has been cancelled");
            return true;
        }
        try {
            ClassicDupe.scheduleRestart();
            sender.sendMessage(Utils.format("&aThe server will restart in 60 seconds"));
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe(e.getMessage());
            sender.sendMessage(Utils.format("&cThere was an error restarting the server."));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return List.of("cancel");
        return new ArrayList<>();
    }
}
