package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorldsizeCMD implements CommandExecutor, TabCompleter {

    private static long overworldSize;
    private static long netherSize;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Utils.cmdMsg("<yellow>Fetching World Sizes..."));
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            overworldSize = Utils.size(Paths.get("", "/worlds/world"));
            netherSize = Utils.size(Paths.get("", "/worlds/world_nether"));
            sender.sendMessage(Utils.cmdMsg("<yellow>Overworld Size: <white>" + overworldSize + " bytes"));
            sender.sendMessage(Utils.cmdMsg("<yellow>Nether Size: <white>" + netherSize + " bytes"));
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
