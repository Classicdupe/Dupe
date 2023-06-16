package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.classicdupe.clans.builders.Warp;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CSSethome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You must be in a clan to set homes"));
            return;
        }
        if(cmem.getLevel() < 2) {
            player.sendMessage(Utils.cmdMsg("<red>You must be either an admin or the owner to set homes in a clan"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        if(args.length == 0) {
            if(clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("<yellow>You must delete your default home with <gold>/clan delhome <yellow>before you can set it again"));
                return;
            }
            Warp warp = new Warp("default", player.getLocation(), 0);
            ClassicDupe.getClanDatabase().setWarp(clan, warp);
            clan.setWarp(warp);
            player.sendMessage(Utils.cmdMsg("<yellow>Set the clans default home to your location"));
        } else {
            if(clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("<yellow>You must delete that home with <gold>/clan delhome " + args[0].toLowerCase() + " <yellow>before you can set it again"));
                return;
            }
            Warp warp = new Warp(args[0].toLowerCase(), player.getLocation(), 0);
            ClassicDupe.getClanDatabase().setWarp(clan, warp);
            clan.setWarp(warp);
            player.sendMessage(Utils.cmdMsg("<yellow>Set " + args[0].toLowerCase() + " to your location"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
