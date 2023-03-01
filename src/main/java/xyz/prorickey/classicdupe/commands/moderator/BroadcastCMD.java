package xyz.prorickey.classicdupe.commands.moderator;

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

public class BroadcastCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.format("&a-----------------------------------------------------"));
        sb.append(Utils.centerText(Utils.format("&eAnnouncement")));
        StringBuilder msg = new StringBuilder();
        for(int i = 0; i < args.length; i++) msg.append(args[i]).append(" ");
        sb.append(Utils.format(msg.toString()));
        sb.append(Utils.format("&a-----------------------------------------------------"));
        ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(p -> p.sendMessage(sb.toString()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
