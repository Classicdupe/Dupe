package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CSLeave extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You aren't in a clan"));
            return;
        }
        if(cmem.getLevel() == 3) {
            player.sendMessage(Utils.cmdMsg("<red>You cannot leave a clan that you own. You must promote someone else to owner before you can leave"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        clan.removePlayer(player);
        ClassicDupe.getClanDatabase().removeClan(cmem);
        cmem.removeClan();
        player.sendMessage(Utils.cmdMsg("<yellowYou have left your clan"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
