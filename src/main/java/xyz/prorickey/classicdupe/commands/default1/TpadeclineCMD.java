package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
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

public class TpadeclineCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("&cYou must include who you are declining"));
            return true;
        }
        Player recipient = Bukkit.getServer().getPlayer(args[0]);
        if(recipient == null || !recipient.isOnline()) {
            p.sendMessage(Utils.cmdMsg("&e" + args[0] + " &cis not currently online"));
            return true;
        }
        if(TpaCMD.tpaRequests.containsKey(recipient) && TpaCMD.tpaRequests.get(recipient) == p) {
            recipient.sendMessage(Utils.cmdMsg("&e" + p.getName() + "&c has declined your teleport request"));
            p.sendMessage(Utils.cmdMsg("&aDeclined &e" + recipient.getName() + "'s &ateleport request"));
            TpaCMD.tpaRequests.remove(recipient);
        } else {
            p.sendMessage(Utils.cmdMsg("&cThat person has not requested to teleport to you"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> tpareqs = new ArrayList<>();
            TpaCMD.tpaRequests.forEach((send, recipient) -> {
                if(recipient == sender) tpareqs.add(send.getName());
            });
            return Utils.tabCompletionsSearch(args[0], tpareqs);
        }
        return new ArrayList<>();
    }
}
