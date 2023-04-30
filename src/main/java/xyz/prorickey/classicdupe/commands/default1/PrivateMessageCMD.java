package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivateMessageCMD implements CommandExecutor, TabCompleter {

    public static final Map<Player, Player> lastInConvo = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a recipient when privately messaging"));
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a message to send them"));
            return true;
        }
        Player recipient = Bukkit.getServer().getPlayer(args[0]);
        if(recipient == null || !recipient.isOnline()) {
            sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
            return true;
        }
        String nameOfSender;
        if(!(sender instanceof Player player)) nameOfSender = "<red>Console<gray>";
        else nameOfSender = player.getName();
        StringBuilder msg = new StringBuilder();
        for(int i = 1; i < args.length; i++) { msg.append(args[i]).append(" "); }
        recipient.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + nameOfSender + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
        sender.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + nameOfSender + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
        if(sender instanceof Player player) {
            lastInConvo.put(recipient, player);
            lastInConvo.put(player, recipient);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
