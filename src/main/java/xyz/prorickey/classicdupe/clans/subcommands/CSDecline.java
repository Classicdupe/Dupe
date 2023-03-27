package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CSDecline extends ClanSub {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a player who's invite you would like to accept"));
            return;
        }
        Player inviter = Bukkit.getPlayer(args[0]);
        if(inviter == null) {
            player.sendMessage(Utils.cmdMsg("&cThat player is currently offline"));
            return;
        }
        AtomicBoolean success = new AtomicBoolean(false);
        for (CSInvite.Invite inv : CSInvite.invites) {
            if(inv.inviteeUUID == player.getUniqueId() && inv.inviterUUID == inviter.getUniqueId()) {
                player.sendMessage(Utils.cmdMsg("&eDeclined the invite to join &6" + inviter.getName() + "'s &eclan"));
                inviter.sendMessage(Utils.cmdMsg("&6" + player.getName() + " &edeclined your invite to join your clan"));
                CSInvite.invites.remove(inv);
                success.set(true);
            }
        }
        if(!success.get()) player.sendMessage(Utils.cmdMsg("&eThat player has not sent you an invite for their clan"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return new ArrayList<>();
        if(args.length == 1) {
            List<String> namesOfHooligans = new ArrayList<>();
            CSInvite.invites.forEach(inv -> { if(inv.inviteeUUID == player.getUniqueId()) namesOfHooligans.add(Bukkit.getOfflinePlayer(inv.inviterUUID).getName()); });
            return TabComplete.tabCompletionsSearch(args[0], namesOfHooligans);
        }
        return new ArrayList<>();
    }
}
