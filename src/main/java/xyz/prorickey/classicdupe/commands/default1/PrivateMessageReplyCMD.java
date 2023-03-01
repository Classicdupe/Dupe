package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

public class PrivateMessageReplyCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("&cYou cannot use this command from console"));
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage(Utils.format("&cYou must include a message to send them"));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for(int i = 1; i < args.length; i++) msg.append(args[i]).append(" ");
        if(PrivateMessageCMD.lastInConvo.containsKey(player)) {
            Player recipient = PrivateMessageCMD.lastInConvo.get(player);
            recipient.sendMessage(Utils.format("&7[PM] &e" + player.getName() + " &7-> &e" + recipient.getName() + " &8\u00BB &7" + msg.toString().trim()));
            player.sendMessage(Utils.format("&7[PM] &e" + player.getName() + " &7-> &e" + recipient.getName() + " &8\u00BB &7" + msg.toString().trim()));
            PrivateMessageCMD.lastInConvo.put(recipient, player);
            PrivateMessageCMD.lastInConvo.put(player, recipient);
        } else player.sendMessage(Utils.format("&cYou have no one to reply to"));
        return true;
    }

}
