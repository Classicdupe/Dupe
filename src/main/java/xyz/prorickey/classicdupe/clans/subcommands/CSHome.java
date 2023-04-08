package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.*;
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class CSHome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        if(Combat.inCombat.containsKey(player)) {
            player.sendMessage(Utils.cmdMsg("&cYou cannot execute this command in combat"));
            return;
        }
        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou can't teleport to any homes if you are not in a clan"));
            return;
        }
        Clan clan = ClanDatabase.getClan(cmem.getClanID());
        if(args.length == 0) {
            if(!clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("&cThat home does not exist"));
                return;
            }
            Warp warp = clan.getWarp("default");
            player.teleport(warp.location);
            player.sendMessage(Utils.cmdMsg("&eYou have been teleported to your clans default home"));
        } else {
            if(!clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("&cThat home does not exist"));
                return;
            }
            Warp warp = clan.getWarp(args[0].toLowerCase());
            player.teleport(warp.location);
            player.sendMessage(Utils.cmdMsg("&eYou have been teleported to &6" + args[0].toLowerCase()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || ClanDatabase.getClanMember(player.getUniqueId()).getClanID() == null) return new ArrayList<>();
        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClanDatabase.getClan(cmem.getClanID()).getWarpNames());
        return new ArrayList<>();
    }
}
