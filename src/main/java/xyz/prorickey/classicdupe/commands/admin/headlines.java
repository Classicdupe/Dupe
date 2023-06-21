package xyz.prorickey.classicdupe.commands.admin;

import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class headlines implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {

            if (command.getName().equals("headline")) {

                if (sender.hasPermission("headliner.perms")) {

                    String message = ChatColor.RED + String.join(" ", args);

                    for (Player player : sender.getServer().getOnlinePlayers()) {
                        player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "ALERT" + ChatColor.YELLOW + ChatColor.BOLD.toString() + "!", message, 10, 70, 20);

                    }

                    return true;
                } else {
                    sender.sendMessage("You don't have permission to use this command! You need headliner.perms");
                    return false;
                }
            }

            if (command.getName().equals("alert")) {
                if (sender.hasPermission("headliner.broadcast")) {
                    String alertPart = ChatColor.RED + ChatColor.BOLD.toString() + "ALERT" + ChatColor.YELLOW + ChatColor.BOLD.toString() + "!";
                    String message = StringUtils.join(args, " ");
                    message = message + " ";

                    // Using the message, calculate how many spaces are needed to center the text on the screen
                    // and make sure not to include Minecraft colors
                    String centeredMessage = centerText(stripColors(message));

                    // Split the centered message into multiple lines if necessary
                    String[] lines = splitMessage(centeredMessage, 80);

                    // Send each line as a separate message
                    for (String line : lines) {
                        for (Player player : sender.getServer().getOnlinePlayers()) {
                            player.sendMessage(alertPart + ChatColor.translateAlternateColorCodes('&', line));
                        }
                    }

                    return true;
                } else {
                    sender.sendMessage("You don't have permission to use this command! You need headliner.broadcast");
                    return false;
                }
            }

            if (command.getName().equals("subhead")) {
                //do the same thing as the title command same colors but make it a actionbar title
                if (sender.hasPermission("headliner.subheader")) {

                    String message = ChatColor.RED + String.join(" ", args);

                    for (Player player : sender.getServer().getOnlinePlayers()) {
                        player.sendActionBar(ChatColor.RED + ChatColor.BOLD.toString() + "ALERT" + ChatColor.YELLOW + ChatColor.BOLD.toString() + "!" + ChatColor.WHITE + " " + message);

                    }

                    return true;
                } else {
                    sender.sendMessage("You don't have permission to use this command! You need headliner.perms");
                    return false;
                }
            }
        }




        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of();
        }
        return new ArrayList<>();
    }

    private String stripColors(String message) {
        return ChatColor.stripColor(message);
    }

    private String centerText(String text) {
        int messageWidth = 80; // Width of the message area
        int spaces = (messageWidth - text.length()) / 2;

        StringBuilder centeredText = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            centeredText.append(" ");
        }

        centeredText.append(text);

        return centeredText.toString();
    }
    private String[] splitMessage(String message, int maxLength) {
        int length = message.length();
        int lines = (int) Math.ceil((double) length / maxLength);
        String[] result = new String[lines];

        for (int i = 0; i < lines; i++) {
            int startIndex = i * maxLength;
            int endIndex = Math.min(startIndex + maxLength, length);
            result[i] = message.substring(startIndex, endIndex);
        }

        return result;
    }
}
