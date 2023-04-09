package xyz.prorickey.classicdupe.commands.moderator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class CspyCMD implements CommandExecutor, TabCompleter {

    public static List<Player> cspyList = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return true;
        }
        if(!cspyList.contains(player)) {
            cspyList.add(player);
            player.sendMessage(Utils.cmdMsg("&aTurned on Command Spy"));
        } else {
            cspyList.remove(player);
            player.sendMessage(Utils.cmdMsg("&cTurned off Command Spy"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
