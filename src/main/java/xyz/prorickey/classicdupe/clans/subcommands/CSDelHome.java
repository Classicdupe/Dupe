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
import java.util.List;

@SuppressWarnings("unused")
public class CSDelHome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You must be in a clan to delete homes"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("<red>You must be either an admin or the owner to delete homes in a clan"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        if(args.length == 0) {
            if(!clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("<yellowThat home does not exist and cannot be deleted"));
                return;
            }
            ClassicDupe.getClanDatabase().delWarp(clan, "default");
            clan.delWarp("default");
            player.sendMessage(Utils.cmdMsg("<yellowDeleted the clans default home"));
        } else {
            if(!clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("<yellowThat home does not exist and cannot be deleted"));
                return;
            }
            ClassicDupe.getClanDatabase().delWarp(clan, args[0].toLowerCase());
            clan.delWarp(args[0].toLowerCase());
            player.sendMessage(Utils.cmdMsg("<yellowDeleted <gold>" + args[0].toLowerCase()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).getClanID() == null) return new ArrayList<>();
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getClanDatabase().getClan(cmem.getClanName()).getWarpNames());
        return new ArrayList<>();
    }
}
