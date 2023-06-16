package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CSChat extends ClanSub implements CommandExecutor, TabCompleter {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return;
        }
        if(ClanDatabase.getClanMember(player.getUniqueId()).getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You are not in a clan"));
            return;
        }
        if(args.length == 0) {
            if(ClanDatabase.isInClanChat(player)) {
                ClanDatabase.removeFromClanChat(player);
                player.sendMessage(Utils.cmdMsg("<red>Disabled clan chat"));
            } else {
                ClanDatabase.putInClanChat(player);
                player.sendMessage(Utils.cmdMsg("<green>Enabled clan chat"));
            }
        } else {
            StringBuilder msg = new StringBuilder();
            for (String arg : args) msg.append(arg).append(" ");
            ClanDatabase.sendToClanChat(msg.toString(), player);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        execute(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return tabComplete(sender, args);
    }
}
