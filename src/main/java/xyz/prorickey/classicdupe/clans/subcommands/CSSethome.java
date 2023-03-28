package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
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
        ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to set homes"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("&cYou must be either an admin or the owner to set homes in a clan"));
            return;
        }
        ClansDatabase.Clan clan = ClansDatabase.getClanByID(cmem.getClanId());
        if(args.length == 0) {
            if(clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("&eYou must delete your default home with &6/clan delhome &ebefore you can set it again"));
                return;
            }
            clan.setWarp("default", player.getLocation());
            player.sendMessage(Utils.cmdMsg("&eSet the clans default home to your location"));
        } else {
            if(clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("&eYou must delete that home with &6/clan delhome " + args[0].toLowerCase() + " &ebefore you can set it again"));
                return;
            }
            clan.setWarp(args[0].toLowerCase(), player.getLocation());
            player.sendMessage(Utils.cmdMsg("&eSet " + args[0].toLowerCase() + " to your location"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
