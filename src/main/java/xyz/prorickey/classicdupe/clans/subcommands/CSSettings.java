package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSSettings extends ClanSub {

    Map<String, String> nameToCode = new HashMap<>() {{
        put("black", "&0");
        put("darkBlue", "&1");
        put("darkGreen", "&2");
        put("darkAqua", "&3");
        put("darkRed", "&4");
        put("darkPurple", "&5");
        put("gold", "&6");
        put("gray", "&7");
        put("darkGray", "&8");
        put("blue", "&9");
        put("green", "&a");
        put("aqua", "&b");
        put("red", "&c");
        put("pink", "&d");
        put("yellow", "&e");
        put("white", "&f");
    }};

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanDatabase.ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou are not in a clan. You can't change the settings of a clan that doesn't exist"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("&cYou must either be an admin or the owner of the clan to execute this command"));
            return;
        }
        ClanDatabase.ClanSettings settings = ClanDatabase.getClanByID(cmem.getClanId()).getClanSettings();
        if(args.length == 0) {
            player.sendMessage(ChatFormat.format("&e&lClan Settings"));
            player.sendMessage(ChatFormat.format("&epublicClan: " + settings.getPublicClan()));
            player.sendMessage(ChatFormat.format("&eclanColor: " + settings.getClanColor() + cmem.getClanName()));
        } else {
            if(args.length == 1) {
                switch(args[0]) {
                    case "publicClan" -> player.sendMessage(Utils.cmdMsg("&e&lClan Setting &6publicClan: " + settings.getPublicClan()));
                    case "clanColor" -> player.sendMessage(Utils.cmdMsg("&e&lClan Setting &6clanColor: " + settings.getClanColor() + cmem.getClanName()));
                    default -> player.sendMessage(Utils.cmdMsg("&cThat setting doesn't exist"));
                }
            } else {
                switch(args[0]) {
                    case "publicClan" -> {
                        if(args[1].equalsIgnoreCase("true")) {
                            settings.setPublicClan(true);
                            player.sendMessage(Utils.cmdMsg("&eYour clan is now public"));
                        } else if(args[1].equalsIgnoreCase("false")) {
                            settings.setPublicClan(false);
                            player.sendMessage(Utils.cmdMsg("&eYour clan is now private"));
                        } else {
                            player.sendMessage(Utils.cmdMsg("&cYou can only set that setting to true or false"));
                        }
                    }
                    case "clanColor" -> {
                        if(nameToCode.containsKey(args[1])) {
                            settings.setClanColor(nameToCode.get(args[1]));
                            player.sendMessage(Utils.cmdMsg("&eYour clan color is now " + nameToCode.get(args[1]) + args[1]));
                        } else {
                            player.sendMessage(Utils.cmdMsg("&cYou must pick one of the following colors: black, darkBlue, darkGreen, darkAqua, darkRed, darkPurple, gold, gray, darkGray, blue, green, aqua, red, pink, yellow or white"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("publicClan", "clanColor"));
        else if(args.length == 2) {
            switch(args[0]) {
                case "publicClan" -> { return TabComplete.tabCompletionsSearch(args[1], List.of("true", "false")); }
                case "clanColor" -> { return TabComplete.tabCompletionsSearch(args[1], nameToCode.keySet().stream().toList()); }
            }
        }
        return new ArrayList<>();
    }
}
