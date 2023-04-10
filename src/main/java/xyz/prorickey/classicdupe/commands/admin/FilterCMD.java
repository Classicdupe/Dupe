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
            Component message = Utils.format("<green>Current Filter Items<gray>(Fullword in red, Partword in green): ");
            StringBuilder sb = new StringBuilder();
            if(ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().size() == 0) {
                sender.sendMessage(Utils.cmdMsg("<green>The filter is currently empty"));
                return true;
            }
            ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().forEach(filterWord -> {
                if(filterWord.fullword) sb.append("<red>").append(filterWord.text).append("<gray>, ");
                else sb.append("<green>").append(filterWord.text).append("<gray>, ");
            });
            sb.delete(sb.length() - 4, sb.length() - 1);
            message = message.append(Utils.format(sb.toString()));
            sender.sendMessage(message);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                Component message = Utils.format("<green>Current Filter Items<gray>(Fullword in red, Partword in green): ");
                StringBuilder sb = new StringBuilder();
                if (ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().size() == 0) {
                    sender.sendMessage(Utils.cmdMsg("<green>The filter is currently empty"));
                    break;
                }
                ClassicDupe.getDatabase().getFilterDatabase().getWordsFromFilter().forEach(filterWord -> {
                    if (filterWord.fullword) sb.append("<red>").append(filterWord.text).append("<gray>, ");
                    else sb.append("<green>").append(filterWord.text).append("<gray>, ");
                });
                sb.delete(sb.length() - 4, sb.length() - 1);
                message = message.append(Utils.format(sb.toString()));
                sender.sendMessage(message);
            }
            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage(Utils.cmdMsg("<red>You must include a word to add to the filter and if it is the full word or part of a word"));
                    break;
                }
                boolean successful = ClassicDupe.getDatabase().getFilterDatabase().addWordToFilter(args[1].toLowerCase(), Boolean.parseBoolean(args[2]));
                if (successful) {
                    sender.sendMessage(Utils.cmdMsg("<green>Added <yellow>" + args[1] + "<green> to the filter as a <yellow>" + (Boolean.parseBoolean(args[2]) ? "Fullword" : "Partword")));
                } else {
                    sender.sendMessage(Utils.cmdMsg("<red>There was an error with adding this word to the filter. Most likely it's already on the filter"));
                }
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(Utils.cmdMsg("<red>You must include a word to remove from the filter"));
                    break;
                }
                ClassicDupe.getDatabase().getFilterDatabase().removeWordFromFilter(args[1].toLowerCase());
                sender.sendMessage(Utils.cmdMsg("<green>That word has been removed from the filter"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return List.of("list", "add", "remove");
        else if(args.length == 3) return List.of("true", "false");
        return new ArrayList<>();
    }
}
