package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSKick extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to kick players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a player to kick"));
            return;
        }
        if(cmem.getLevel() < 1) {
            player.sendMessage(Utils.cmdMsg("&cYou must be either an admin or the owner to kick people from a clan"));
            return;
        }
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!ClansDatabase.getClanByID(cmem.getClanId()).getClanMemberUUIDs().contains(offPlayer.getUniqueId())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        ClansDatabase.ClanMember pmem = ClansDatabase.getClanMember(offPlayer.getUniqueId());
        if(!Objects.equals(pmem.getClanId(), cmem.getClanId())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        if(cmem.getLevel() < 2 && pmem.getLevel() > 1) {
            player.sendMessage(Utils.cmdMsg("&cYou must be the owner of the clan to kick that player"));
            return;
        }
        pmem.removeClan();
        if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been kicked from your clan by &6" + player.getName()));
        player.sendMessage(Utils.cmdMsg("&eKicked &6" + offPlayer.getName() + "&e from your clan"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
