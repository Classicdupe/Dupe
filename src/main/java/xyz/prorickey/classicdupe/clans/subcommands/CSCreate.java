package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.Clans;
import xyz.prorickey.classicdupe.clans.Clan;
import xyz.prorickey.classicdupe.clans.ClanMember;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.proutils.ChatFormat;

import java.util.ArrayList;
import java.util.List;

public class CSCreate extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanID() != null) {
            player.sendMessage(Utils.cmdMsg("&cYou are already in a clan! You must leave it before creating another one"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a clan name"));
            return;
        }
        if(ChatColor.stripColor(ChatFormat.format(args[0])).length() != args[0].length()) {
            player.sendMessage(Utils.cmdMsg("&cYour clan cannot include color codes in it's name"));
            return;
        }
        if(args[0].length() > ClanDatabase.getGlobalConfig().getInt("clans.maxChar")) {
            player.sendMessage(Utils.cmdMsg("&cYour clan name can only be " + ClanDatabase.getGlobalConfig().getInt("clans.maxChar") + " characters long"));
            return;
        }
        Clan clan = ClanDatabase.getClan(args[0]);
        if(clan != null) {
            player.sendMessage(Utils.cmdMsg("&cThat clan already exists! Please pick a unique name"));
            return;
        }
        ClanDatabase.createClan(args[0], player);
        player.sendMessage(Utils.cmdMsg("&eCreated the clan &6" + args[0]));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
