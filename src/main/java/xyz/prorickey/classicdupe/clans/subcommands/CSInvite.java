package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

@SuppressWarnings("unused")
public class CSInvite extends ClanSub {

    public static final List<Invite> invites = new ArrayList<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You can't invite people to your imaginary clan. You must create one with /clan create"));
            return;
        }
        if(cmem.getLevel() < 1) {
            player.sendMessage(Utils.cmdMsg("<red>You must at least be vip to invite people to a clan"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must provide a player to invite"));
            return;
        }
        Player p = Bukkit.getPlayer(args[0]);
        if(p == null) {
            player.sendMessage(Utils.cmdMsg("<red>That player is currently offline"));
            return;
        }
        ClanMember pmem = ClassicDupe.getClanDatabase().getClanMember(p.getUniqueId());
        if(pmem.getClanID() != null) {
            player.sendMessage(Utils.cmdMsg("<red>That player is already in a clan"));
            return;
        }
        Invite invite = new Invite(player.getUniqueId(), p.getUniqueId());
        invites.add(invite);
        player.sendMessage(Utils.cmdMsg("<yellow>Invite sent to <gold>" + p.getName()));
        p.sendMessage(Utils.cmdMsg("<yellow>Recieved an invite to join <gold>" + cmem.getClanName() + "<yellow> from <gold>" + player.getName() + " ")
                .append(Utils.format("<dark_gray>[<green><b>ACCEPT<dark_gray>]")
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan accept " + player.getName())))
                .append(Component.text(" "))
                .append(Utils.format("<dark_gray>[<red><b>DECLINE<dark_gray>]"))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan decline " + player.getName())));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }

    public static class InviteTask extends BukkitRunnable {
        @Override
        public void run() {
            List<Invite> tempInvites = new ArrayList<>(invites);
            tempInvites.forEach(invite -> {
                if((invite.inviteSent + (1000*60)) < System.currentTimeMillis()) {
                    invites.remove(invite);
                    OfflinePlayer inviter = Bukkit.getOfflinePlayer(invite.inviterUUID);
                    OfflinePlayer invitee = Bukkit.getOfflinePlayer(invite.inviteeUUID);
                    if(inviter.isOnline()) Bukkit.getPlayer(invite.inviterUUID).sendMessage(Utils.cmdMsg("<yellow>Invite sent to <gold>" + invitee.getName() + " <yellow>has expired"));
                    if(invitee.isOnline()) Bukkit.getPlayer(invite.inviteeUUID).sendMessage(Utils.cmdMsg("<yellow>Invite from <gold>" + inviter.getName() + "<yellow> has expired"));
                }
            });
        }
    }

    public static class Invite {
        public final UUID inviterUUID;
        public final UUID inviteeUUID;
        public final long inviteSent;
        public Invite(UUID inviter, UUID invitee) {
            this.inviterUUID = inviter;
            this.inviteeUUID = invitee;
            this.inviteSent = System.currentTimeMillis();
        }
    }
}
