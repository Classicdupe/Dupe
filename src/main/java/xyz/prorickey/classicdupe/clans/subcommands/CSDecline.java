package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class CSDecline extends ClanSub {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must provide a player who's invite you would like to accept"));
            return;
        }
        Player inviter = Bukkit.getPlayer(args[0]);
        if(inviter == null) {
            player.sendMessage(Utils.cmdMsg("<red>That player is currently offline"));
            return;
        }
        AtomicBoolean success = new AtomicBoolean(false);
        final List<CSInvite.Invite> tempInvites = new ArrayList<>(CSInvite.invites);
        for (CSInvite.Invite inv : tempInvites) {
            if(inv.inviteeUUID == player.getUniqueId() && inv.inviterUUID == inviter.getUniqueId()) {
                player.sendMessage(Utils.cmdMsg("<yellow>Declined the invite to join <gold>" + inviter.getName() + "'s <yellowclan"));
                inviter.sendMessage(Utils.cmdMsg("<gold>" + player.getName() + " <yellowdeclined your invite to join your clan"));
                CSInvite.invites.remove(inv);
                success.set(true);
            }
        }
        if(!success.get()) player.sendMessage(Utils.cmdMsg("<yellowThat player has not sent you an invite for their clan"));
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
