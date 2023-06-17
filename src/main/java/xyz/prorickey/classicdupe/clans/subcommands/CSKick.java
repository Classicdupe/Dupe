package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import java.util.Objects;

@SuppressWarnings("unused")
public class CSKick extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You must be in a clan to kick players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must provide a player to kick"));
            return;
        }
        if(cmem.getLevel() < 1) {
            player.sendMessage(Utils.cmdMsg("<red>You must be either an admin or the owner to kick people from a clan"));
            return;
        }
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!ClassicDupe.getClanDatabase().getClan(cmem.getClanID()).getMembers().contains(offPlayer)) {
            player.sendMessage(Utils.cmdMsg("<red>That player is not in your clan"));
            return;
        }
        ClanMember pmem = ClassicDupe.getClanDatabase().getClanMember(offPlayer.getUniqueId());
        if(!Objects.equals(pmem.getClanID(), cmem.getClanID())) {
            player.sendMessage(Utils.cmdMsg("<red>That player is not in your clan"));
            return;
        }
        if(cmem.getLevel() < 2 && pmem.getLevel() > 1) {
            player.sendMessage(Utils.cmdMsg("<red>You must be the owner of the clan to kick that player"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        clan.removePlayer(pmem.getOffPlayer());
        pmem.removeClan();
        ClassicDupe.getClanDatabase().removeClan(pmem);
        if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("<yellow>You have been kicked from your clan by <gold>" + player.getName()));
        player.sendMessage(Utils.cmdMsg("<yellow>Kicked <gold>" + offPlayer.getName() + "<yellow> from your clan"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
