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

public class PrivateMessageReplyCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot use this command from console"));
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a message to send them"));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (String arg : args) msg.append(arg).append(" ");
        if(PrivateMessageCMD.lastInConvo.containsKey(player)) {
            Player recipient = PrivateMessageCMD.lastInConvo.get(player);
            recipient.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + player.getName() + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
            player.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + player.getName() + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
            PrivateMessageCMD.lastInConvo.put(recipient, player);
            PrivateMessageCMD.lastInConvo.put(player, recipient);
        } else player.sendMessage(Utils.cmdMsg("<red>You have no one to reply to"));
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
