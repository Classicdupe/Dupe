package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSDemote extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanDatabase.ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to demote players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a player to demote"));
            return;
        }
        ClanDatabase.Clan clan = ClanDatabase.getClanByID(cmem.getClanId());
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!clan.getClanMemberUUIDs().contains(offPlayer.getUniqueId())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        ClanDatabase.ClanMember pmem = ClanDatabase.getClanMember(offPlayer.getUniqueId());
        if(!Objects.equals(pmem.getClanId(), cmem.getClanId())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        if(pmem.getLevel() == 0) {
            player.sendMessage(Utils.cmdMsg("&cThat player is already a default. You cannot demote them further"));
            return;
        }
        if(pmem.getLevel() == 1) {
            if(cmem.getLevel() < 3) {
                player.sendMessage(Utils.cmdMsg("&cYou must be an admin or the owner to demote players"));
                return;
            }
            pmem.setLevel(0);
            player.sendMessage(Utils.cmdMsg("&eDemoted &6" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been demoted in your clan by &6" + player.getName()));
        } else if(pmem.getLevel() == 2) {
            if(cmem.getLevel() != 3) {
                player.sendMessage(Utils.cmdMsg("&cYou must be the owner of the clan to demote admins"));
                return;
            }
            pmem.setLevel(1);
            player.sendMessage(Utils.cmdMsg("&eDemoted &6" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been demoted in your clan by &6" + player.getName()));
        } else if(pmem.getLevel() == 3) {
            player.sendMessage(Utils.cmdMsg("&cYou cannot demote the owner of your clan"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
