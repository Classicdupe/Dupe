package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CSHelp extends ClanSub {

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(Utils.format("<green> \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D <yellow<b>Clans <green>\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));

        sender.sendMessage(Utils.format("<yellow/clan help <gray>- <gold>To display the help section"));
        sender.sendMessage(Utils.format("<yellow/clan info [clan] <gray>- <gold>To display info about a clan"));
        sender.sendMessage(Utils.format("<yellow/clan chat [message] <gray>- <gold>To toggle clan chat or send a message"));
        sender.sendMessage(Utils.format("<yellow/clan create <clanName> <gray>- <gold>To create a clan"));
        sender.sendMessage(Utils.format("<yellow/clan delete [confirm] <gray>- <gold>To delete your clan"));
        sender.sendMessage(Utils.format("<yellow/clan invite <player> <gray>- <gold>To invite a player to the clan"));
        sender.sendMessage(Utils.format("<yellow/clan accept <player> <gray>- <gold>To accept an invite to a clan"));
        sender.sendMessage(Utils.format("<yellow/clan decline <player> <gray>- <gold>To decline an invite to a clan"));
        sender.sendMessage(Utils.format("<yellow/clan home [home] <gray>- <gold>To teleport to a home"));
        sender.sendMessage(Utils.format("<yellow/clan sethome [home] <gray>- <gold>To set a home for the clan"));
        sender.sendMessage(Utils.format("<yellow/clan delhome [home] <gray>- <gold>To delete a home"));
        sender.sendMessage(Utils.format("<yellow/clan kick <player> <gray>- <gold>To kick a player from the clan"));
        sender.sendMessage(Utils.format("<yellow/clan leave <gray>- <gold>To leave a clan"));
        sender.sendMessage(Utils.format("<yellow/clan promote <player> <gray>- <gold>To promote a player in the clan"));
        sender.sendMessage(Utils.format("<yellow/clan demote <player> <gray>- <gold>To demote a player in the clan"));
        sender.sendMessage(Utils.format("<yellow/clan settings <gray>- <gold>Settings for the clan"));

        sender.sendMessage(Utils.format("<green>\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D"));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
