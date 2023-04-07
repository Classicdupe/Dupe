package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class CSDelHome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanDatabase.ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to delete homes"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("&cYou must be either an admin or the owner to delete homes in a clan"));
            return;
        }
        ClanDatabase.Clan clan = ClanDatabase.getClanByID(cmem.getClanId());
        if(args.length == 0) {
            if(!clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("&eThat home does not exist and cannot be deleted"));
                return;
            }
            clan.delWarp("default");
            player.sendMessage(Utils.cmdMsg("&eDeleted the clans default home"));
        } else {
            if(!clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("&eThat home does not exist and cannot be deleted"));
                return;
            }
            clan.delWarp(args[0].toLowerCase());
            player.sendMessage(Utils.cmdMsg("&eDeleted &6" + args[0].toLowerCase()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || ClanDatabase.getClanMember(player.getUniqueId()).getClanId() == null) return new ArrayList<>();
        ClanDatabase.ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClanDatabase.getClanByID(cmem.getClanId()).getWarpNames());
        return new ArrayList<>();
    }
}
