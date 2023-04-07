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

public class CSHome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanDatabase.ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou can't teleport to any homes if you are not in a clan"));
            return;
        }
        ClanDatabase.Clan clan = ClanDatabase.getClanByID(cmem.getClanId());
        if(args.length == 0) {
            if(!clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("&cThat home does not exist"));
                return;
            }
            ClanDatabase.Warp warp = clan.getWarpMap().get("default");
            player.teleport(warp.location);
            player.sendMessage(Utils.cmdMsg("&eYou have been teleported to your clans default home"));
        } else {
            if(!clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("&cThat home does not exist"));
                return;
            }
            ClanDatabase.Warp warp = clan.getWarpMap().get(args[0].toLowerCase());
            player.teleport(warp.location);
            player.sendMessage(Utils.cmdMsg("&eYou have been teleported to &6" + args[0].toLowerCase()));
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
