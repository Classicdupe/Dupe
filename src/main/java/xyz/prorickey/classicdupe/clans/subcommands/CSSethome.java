package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.*;
import xyz.prorickey.proutils.ChatFormat;

import java.util.ArrayList;
import java.util.List;

public class CSSethome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to set homes"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("&cYou must be either an admin or the owner to set homes in a clan"));
            return;
        }
        Clan clan = ClanDatabase.getClan(cmem.getClanID());
        if(args.length == 0) {
            if(clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("&eYou must delete your default home with &6/clan delhome &ebefore you can set it again"));
                return;
            }
            Warp warp = new Warp("default", player.getLocation(), 0);
            ClanDatabase.setWarp(clan, warp);
            clan.setWarp(warp);
            player.sendMessage(Utils.cmdMsg("&eSet the clans default home to your location"));
        } else {
            if(clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("&eYou must delete that home with &6/clan delhome " + args[0].toLowerCase() + " &ebefore you can set it again"));
                return;
            }
            Warp warp = new Warp(args[0].toLowerCase(), player.getLocation(), 0);
            ClanDatabase.setWarp(clan, warp);
            clan.setWarp(warp);
            player.sendMessage(Utils.cmdMsg("&eSet " + args[0].toLowerCase() + " to your location"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
