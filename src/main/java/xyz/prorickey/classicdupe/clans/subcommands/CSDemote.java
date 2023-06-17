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
public class CSDemote extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You must be in a clan to demote players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must provide a player to demote"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!clan.getMembers().contains(offPlayer)) {
            player.sendMessage(Utils.cmdMsg("<red>That player is not in your clan"));
            return;
        }
        ClanMember pmem = ClassicDupe.getClanDatabase().getClanMember(offPlayer.getUniqueId());
        if(!Objects.equals(pmem.getClanID(), cmem.getClanID())) {
            player.sendMessage(Utils.cmdMsg("<red>That player is not in your clan"));
            return;
        }
        if(pmem.getLevel() == 0) {
            player.sendMessage(Utils.cmdMsg("<red>That player is already a default. You cannot demote them further"));
            return;
        }
        if(pmem.getLevel() == 1) {
            if(cmem.getLevel() < 3) {
                player.sendMessage(Utils.cmdMsg("<red>You must be an admin or the owner to demote players"));
                return;
            }
            pmem.setLevel(0);
            clan.removeVip(pmem.getOffPlayer());
            clan.addDefault(pmem.getOffPlayer());
            ClassicDupe.getClanDatabase().setPlayerLevel(pmem, 0);
            player.sendMessage(Utils.cmdMsg("<yellowDemoted <gold>" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("<yellowYou have been demoted in your clan by <gold>" + player.getName()));
        } else if(pmem.getLevel() == 2) {
            if(cmem.getLevel() != 3) {
                player.sendMessage(Utils.cmdMsg("<red>You must be the owner of the clan to demote admins"));
                return;
            }
            pmem.setLevel(1);
            clan.removeAdmin(pmem.getOffPlayer());
            clan.addVip(pmem.getOffPlayer());
            ClassicDupe.getClanDatabase().setPlayerLevel(pmem, 1);
            player.sendMessage(Utils.cmdMsg("<yellowDemoted <gold>" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("<yellowYou have been demoted in your clan by <gold>" + player.getName()));
        } else if(pmem.getLevel() == 3) {
            player.sendMessage(Utils.cmdMsg("<red>You cannot demote the owner of your clan"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
