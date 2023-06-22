package xyz.prorickey.classicdupe.commands.perk;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class NicknameCMD implements CommandExecutor, TabCompleter {

    private static String chars = "abcdefghijklmnopqrstuvxwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789_";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId()).resetNickname();
            sender.sendMessage(Utils.cmdMsg("<green>Reset your nickname"));
            return true;
        }
        if(args.length > 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot have spaces in your nickname"));
            return true;
        }
        if(!args[0].matches("^[A-Za-z0-9_&]+$")) {
            sender.sendMessage(Utils.cmdMsg("<red>Your nickname cannot have any special characters in it"));
            return true;
        }
        Component nickname = Utils.format(Utils.convertColorCodesToAdventure(args[0]));
        if(PlainTextComponentSerializer.plainText().serialize(nickname).length() > 20) {
            sender.sendMessage(Utils.cmdMsg("<red>Your nickname must be under 20 characters"));
            return true;
        }
        ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId()).setNickname(Utils.convertColorCodesToAdventure(args[0]));
        p.sendMessage(Utils.cmdMsg("<green>Set your nickname to " + Utils.convertColorCodesToAdventure(args[0])));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
