package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class RulesCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Utils.format("<green> \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D <yellow><b>Rules <green>\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));

        sender.sendMessage(Utils.format("<yellow>1. No Cheating - We disallow the use of hacks, glitches and other ways to get an unfair advantage over other players."));
        sender.sendMessage(Utils.format("<yellow>2. Respect Other Players - This means you are not allowed to say slurs or spam in the chat, do not bully or harrass other players and make sure your username and skin are appropriate."));
        sender.sendMessage(Utils.format("<yellow>3. Respect The Server - Do not build any lag machines and do not try to bypass a ban by using an alternate account."));
        sender.sendMessage(Utils.format("<yellow>4. Post Appropriate Media - Do not most media that is not appropriate."));
        sender.sendMessage(Utils.format("<yellow>5. Don't grief spawn and don't make overly annoying structures - Do not grief spawn and don't make super annoying structures like lava casts."));

        sender.sendMessage(Utils.format("<green>\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
