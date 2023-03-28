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
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan info [clan] &7- &6To display info about a clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan create <clanName> &7- &6To create a clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan delete [confirm] &7- &6To delete your clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan invite <player> &7- &6To invite a player to the clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan accept <player> &7- &6To accept an invite to a clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan decline <player> &7- &6To decline an invite to a clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan home [home] &7- &6To teleport to a home")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan sethome [home] &7- &6To set a home for the clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan delhome [home] &7- &6To delete a home")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan kick <player> &7- &6To kick a player from the clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan leave &7- &6To leave a clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan promote <player> &7- &6To promote a player in the clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan demote <player> &7- &6To demote a player in the clan")));
        sender.sendMessage(Component.text(ChatFormat.format("&e/clan settings &7- &6Settings for the clan")));

        sender.sendMessage(ChatFormat.format("&a\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D"));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
