package xyz.prorickey.classicdupe.commands.admin;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class FilterCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            Component message = Component.text(Utils.format("&aCurrent Filter Items&7(Fullword in red, Partword in green): "));
            StringBuilder sb = new StringBuilder();
            if(ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().size() == 0) {
                sender.sendMessage(Utils.format("&aThe filter is currently empty."));
                return true;
            }
            ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().forEach(filterWord -> {
                if(filterWord.fullword) sb.append("&c").append(filterWord.text).append("&7, ");
                else sb.append("&a").append(filterWord.text).append("&7, ");
            });
            sb.delete(sb.length() - 4, sb.length() - 1);
            message = message.append(Component.text(Utils.format(sb.toString())));
            sender.sendMessage(message);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                Component message = Component.text(Utils.format("&aCurrent Filter Items&7(Fullword in red, Partword in green): "));
                StringBuilder sb = new StringBuilder();
                if (ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().size() == 0) {
                    sender.sendMessage(Utils.format("&aThe filter is currently empty."));
                    break;
                }
                ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().forEach(filterWord -> {
                    if (filterWord.fullword) sb.append("&c").append(filterWord.text).append("&7, ");
                    else sb.append("&a").append(filterWord.text).append("&7, ");
                });
                sb.delete(sb.length() - 4, sb.length() - 1);
                message = message.append(Component.text(Utils.format(sb.toString())));
                sender.sendMessage(message);
            }
            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage(Utils.format("&cYou must include a word to add to the filter and if it is the full word or part of a word."));
                    break;
                }
                boolean successful = ClassicDupe.getDatabase().getFilterDatabase().addWordToFilter(args[1].toLowerCase(), Boolean.parseBoolean(args[2]));
                if (successful) {
                    sender.sendMessage(Utils.format("&aAdded '&e" + args[1] + "&a' to the filter as a &e" + (Boolean.parseBoolean(args[2]) ? "Fullword" : "Partword")));
                } else {
                    sender.sendMessage(Utils.format("&cThere was an error with adding this word to the filter. Most likely it's already on the filter."));
                }
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(Utils.format("&cYou must include a word to remove from the filter."));
                    break;
                }
                ClassicDupe.getDatabase().getFilterDatabase().removeWordFromFilter(args[1].toLowerCase());
                sender.sendMessage(Utils.format("&aThat word has been removed from the filter."));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of("list", "add", "remove");
        } else if(args.length == 3) {
            return List.of("true", "false");
        }
        return new ArrayList<>();
    }
}
