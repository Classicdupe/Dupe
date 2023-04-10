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
        StringBuilder msg = new StringBuilder();
        for (String arg : args) msg.append(arg).append(" ");
        ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(p -> {
            p.sendMessage(Utils.format("<green>-----------------------------------------------------"));
            p.sendMessage(Utils.format("<yellow>" + Utils.centerText("Announcement")));
            p.sendMessage(Utils.format(msg.toString()));
            p.sendMessage(Utils.format("<green>-----------------------------------------------------"));
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
