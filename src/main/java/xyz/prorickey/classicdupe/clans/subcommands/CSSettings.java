package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CSSettings extends ClanSub {

    final Map<String, String> nameToCode = new HashMap<>() {{
        put("black", "<black>");
        put("darkBlue", "<dark_blue>");
        put("darkGreen", "<dark_green>");
        put("darkAqua", "<dark_aqua>");
        put("darkRed", "<dark_red>");
        put("darkPurple", "<dark_purple>");
        put("gold", "<gold>");
        put("gray", "<gray>");
        put("darkGray", "<dark_gray>");
        put("blue", "<blue>");
        put("green", "<green>");
        put("aqua", "<aqua>");
        put("red", "<red>");
        put("pink", "<light_purple>");
        put("yellow", "<yellow>");
        put("white", "<white>");
    }};

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You are not in a clan. You can't change the settings of a clan that doesn't exist"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("<red>You must either be an admin or the owner of the clan to execute this command"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        if(args.length == 0) {
            player.sendMessage(Utils.format("<yellow><bold>Clan Settings"));
            player.sendMessage(Utils.format("<yellow>publicClan: " + clan.getPublicClan()));
            player.sendMessage(Utils.format("<yellow>clanColor: " + clan.getClanColor() + cmem.getClanName()));
        } else {
            if(args.length == 1) {
                switch(args[0]) {
                    case "publicClan" -> player.sendMessage(Utils.cmdMsg("<yellow<b>Clan Setting <gold>publicClan: " + clan.getPublicClan()));
                    case "clanColor" -> player.sendMessage(Utils.cmdMsg("<yellow<b>Clan Setting <gold>clanColor: " + clan.getClanColor() + cmem.getClanName()));
                    default -> player.sendMessage(Utils.cmdMsg("<red>That setting doesn't exist"));
                }
            } else {
                switch(args[0]) {
                    case "publicClan" -> {
                        if(args[1].equalsIgnoreCase("true")) {
                            clan.setPublicClan(true);
                            ClassicDupe.getClanDatabase().setPublicClan(clan, true);
                            player.sendMessage(Utils.cmdMsg("<yellowYour clan is now public"));
                        } else if(args[1].equalsIgnoreCase("false")) {
                            clan.setPublicClan(false);
                            ClassicDupe.getClanDatabase().setPublicClan(clan, false);
                            player.sendMessage(Utils.cmdMsg("<yellowYour clan is now private"));
                        } else {
                            player.sendMessage(Utils.cmdMsg("<red>You can only set that setting to true or false"));
                        }
                    }
                    case "clanColor" -> {
                        if(nameToCode.containsKey(args[1])) {
                            clan.setClanColor(nameToCode.get(args[1]));
                            ClassicDupe.getClanDatabase().setClanColor(clan, nameToCode.get(args[1]));
                            player.sendMessage(Utils.cmdMsg("<yellowYour clan color is now " + nameToCode.get(args[1]) + args[1]));
                        } else {
                            player.sendMessage(Utils.cmdMsg("<red>You must pick one of the following colors: black, darkBlue, darkGreen, darkAqua, darkRed, darkPurple, gold, gray, darkGray, blue, green, aqua, red, pink, yellow or white"));
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
