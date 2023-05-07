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
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class TpacceptCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include who you are accepting"));
            return true;
        }
        Player recipient = Bukkit.getServer().getPlayer(args[0]);
        if(recipient == null || !recipient.isOnline()) {
            p.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
            return true;
        }
        if(TpaCMD.tpaRequests.containsKey(recipient) && TpaCMD.tpaRequests.get(recipient) == p) {
            if(Combat.inCombat.containsKey(recipient)) {
                p.sendMessage(Utils.cmdMsg("<red>That player is currently in combat and cannot teleport"));
                return true;
            }
            TpaCMD.saveLocation(recipient);
            recipient.teleport(p.getLocation());
            recipient.sendMessage(Utils.cmdMsg("<green>Teleported you to <yellow>" + p.getName()));
            p.sendMessage(Utils.cmdMsg("<yellow>" + recipient.getName() + "<green> has teleported to you"));
            TpaCMD.tpaRequests.remove(recipient);
            TpaCMD.tpaRequestTimes.remove(recipient);
        } else {
            p.sendMessage(Utils.cmdMsg("<red>That person has not requested to teleport to you"));
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
            return TabComplete.tabCompletionsSearch(args[0], tpareqs);
        }
        return new ArrayList<>();
    }
}
