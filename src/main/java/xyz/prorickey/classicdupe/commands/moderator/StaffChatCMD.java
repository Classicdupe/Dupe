package xyz.prorickey.classicdupe.commands.moderator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.ChatFormat;

public class StaffChatCMD implements CommandExecutor, TabCompleter {

    public static List<Player> staffChatPlayers = new ArrayList<>();

    public static void sendToStaffChat(String text) {
        ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(p -> {
            if(p.hasPermission("mod.staffchat")) p.sendMessage(ChatFormat.format(text));
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console."));
            return true;
        }
        if(args.length == 0) {
            if(staffChatPlayers.contains(p)) {
                staffChatPlayers.remove(p);
                p.sendMessage(Utils.cmdMsg("&cTurned off StaffChat"));
            } else {
                staffChatPlayers.add(p);
                p.sendMessage(Utils.cmdMsg("&aTurned on StaffChat"));
            }
        } else {
            if(args[0].equalsIgnoreCase("on")) {
                if(staffChatPlayers.contains(p)) {
                    p.sendMessage(Utils.cmdMsg("&cYour staffchat is already on"));
                    return true;
                }
                staffChatPlayers.add(p);
                p.sendMessage(Component.text(Utils.cmdMsg("&aTurned your staffchat on")));
            } else if(args[0].equalsIgnoreCase("off")) {
                if(!staffChatPlayers.contains(p)) {
                    p.sendMessage(Utils.cmdMsg("&cYour staffchat is already off"));
                    return true;
                }
                staffChatPlayers.remove(p);
                p.sendMessage(Utils.cmdMsg("&aTurned your staffchat off"));
            }
        }
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return List.of("on", "off");
        return new ArrayList<>();
    }
    
}
