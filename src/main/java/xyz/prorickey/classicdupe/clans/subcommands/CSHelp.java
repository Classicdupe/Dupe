package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.proutils.ChatFormat;

import java.util.ArrayList;
import java.util.List;

public class CSHelp extends ClanSub {

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(ChatFormat.format("&a \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D &e&lClans &a\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));

        sender.sendMessage(Component.text(ChatFormat.format("&e/clan help &7- &6To display the help section")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan create <clanName> &7- &6To create a clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan delete [confirm] &7- &6To delete your clan")));

        sender.sendMessage(ChatFormat.format("&a\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D"));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {

        return new ArrayList<>();
    }
}
