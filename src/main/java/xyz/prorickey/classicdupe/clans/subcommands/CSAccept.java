package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.Bukkit;
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
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class CSAccept extends ClanSub {

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
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() != null) {
            player.sendMessage(Utils.cmdMsg("<red>You are already in a clan. You must leave before joining another."));
            return;
        }
        AtomicBoolean success = new AtomicBoolean(false);
        final List<CSInvite.Invite> tempInvites = new ArrayList<>(CSInvite.invites);
        for (CSInvite.Invite inv : tempInvites) {
            if(inv.inviteeUUID == player.getUniqueId() && inv.inviterUUID == inviter.getUniqueId()) {
                Clan clan = ClassicDupe.getClanDatabase().getClan(ClassicDupe.getClanDatabase().getClanMember(inviter.getUniqueId()).getClanID());
                ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).setClan(clan, 0);
                ClassicDupe.getClanDatabase().setClan(player.getUniqueId(), clan);
                clan.addDefault(player);
                clan.addPlayer(player);
                player.sendMessage(Utils.cmdMsg("<yellow>Successfully joined <gold>" + clan.getClanName()));
                inviter.sendMessage(Utils.cmdMsg("<gold>" + player.getName() + " <yellow>accepted your invite and successfully joined the clan"));
                CSInvite.invites.remove(inv);
                success.set(true);
            }
        }
        if(!success.get()) player.sendMessage(Utils.cmdMsg("<yellow>That player has not sent you an invite for their clan"));
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
