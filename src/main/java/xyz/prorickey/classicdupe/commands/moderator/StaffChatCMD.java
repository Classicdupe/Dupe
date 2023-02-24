package xyz.prorickey.classicdupe.commands.moderator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import xyz.prorickey.classicdupe.Utils;

public class StaffChatCMD implements CommandExecutor, TabCompleter {

    public static List<Player> staffChatPlayers = new ArrayList();

    public static void sendToStaffChat(String text) {
        staffChatPlayers.forEach(p -> p.sendMessage(Component.text(Utils.format(text))));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Component.text(Utils.format("&cYou cannot execute this command from console.")));
            return true;
        }
        if(args.length == 0) {
            if(staffChatPlayers.contains(p)) {
                staffChatPlayers.remove(p);
                p.sendMessage(Component.text(Utils.format("&cTurned off StaffChat")));
            } else {
                staffChatPlayers.add(p);
                p.sendMessage(Component.text(Utils.format("&aTurned on StaffChat")));
            }
        } else {
            if(args[0].toLowerCase().equals("on")) {
                if(staffChatPlayers.contains(p)) {
                    p.sendMessage(Component.text(Utils.format("&cYour staffchat is already on")));
                    return true;
                }
                staffChatPlayers.add(p);
                p.sendMessage(Component.text(Utils.format("&aTurned your staffchat on")));
            } else if(args[0].toLowerCase().equals("off")) {
                if(!staffChatPlayers.contains(p)) {
                    p.sendMessage(Component.text(Utils.format("&cYour staffchat is already off")));
                    return true;
                }
                staffChatPlayers.remove(p);
                p.sendMessage(Component.text(Utils.format("&aTurned your staffchat off")));
            }
        }
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return List.of("on", "off");
        return new ArrayList();
    }
    
}
