package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class TpacancelCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(!TpaCMD.tpaRequests.containsKey(p)) {
            p.sendMessage(Utils.cmdMsg("<red>You don't currently have any TPA requests"));
            return true;
        }
        p.sendMessage(Utils.cmdMsg("<green>Cancelled TPA to <yellow>" + TpaCMD.tpaRequests.get(p).getName()));
        TpaCMD.tpaRequests.get(p).sendMessage(Utils.cmdMsg("<green>TPA from <yellow>" + p.getName() + "<green> has been cancelled"));
        TpaCMD.tpaRequests.remove(p);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
