package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.Clan;
import xyz.prorickey.classicdupe.clans.ClanMember;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClanDatabase;

import java.util.ArrayList;
import java.util.List;

public class CSLeave extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You aren't in a clan"));
            return;
        }
        if(cmem.getLevel() == 3) {
            player.sendMessage(Utils.cmdMsg("<red>You cannot leave a clan that you own. You must promote someone else to owner before you can leave"));
            return;
        }
        Clan clan = ClanDatabase.getClan(cmem.getClanID());
        clan.removePlayer(player);
        ClanDatabase.removeClan(cmem);
        cmem.removeClan();
        player.sendMessage(Utils.cmdMsg("<yellowYou have left your clan"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
