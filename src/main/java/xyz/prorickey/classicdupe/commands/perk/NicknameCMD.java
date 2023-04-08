package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class NicknameCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return true;
        }
        if(args.length < 1) {
            ClassicDupe.getDatabase().getPlayerDatabase().resetNickname(p.getUniqueId().toString());
            sender.sendMessage(Utils.cmdMsg("&aReset your nickname"));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (String arg : args) msg.append(arg).append(" ");
        if(ChatColor.stripColor(msg.toString()).length() > 20) {
            sender.sendMessage(Utils.cmdMsg("&cYour nickname must be under 20 characters"));
            return true;
        }
        ClassicDupe.getDatabase().getPlayerDatabase().setNickname(p.getUniqueId().toString(), msg.toString());
        p.sendMessage(Utils.cmdMsg("&aSet your nickname to " + msg));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
