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
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class CSInvite extends ClanSub {

    public static List<Invite> invites = new ArrayList<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou can't invite people to your imaginary clan. You must create one with /clan create"));
            return;
        }
        if(cmem.getLevel() < 1) {
            player.sendMessage(Utils.cmdMsg("&cYou must at least be vip to invite people to a clan"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a player to invite"));
            return;
        }
        Player p = Bukkit.getPlayer(args[0]);
        if(p == null) {
            player.sendMessage(Utils.cmdMsg("&cThat player is currently offline"));
            return;
        }
        ClansDatabase.ClanMember pmem = ClansDatabase.getClanMember(p.getUniqueId());
        if(pmem.getClanId() != null) {
            player.sendMessage(Utils.cmdMsg("&cThat player is already in a clan"));
            return;
        }
        Invite invite = new Invite(player.getUniqueId(), p.getUniqueId());
        invites.add(invite);
        player.sendMessage(Utils.cmdMsg("&eInvite sent to &6" + p.getName()));
        p.sendMessage(Component.text(Utils.cmdMsg("&eRecieved an invite to join &6" + cmem.getClanName() + "&e from &6" + player.getName() + " "))
                .append(Component.text(ChatFormat.format("&8[&a&lACCEPT&8]"))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan accept " + player.getName())))
                .append(Component.text(" "))
                .append(Component.text(ChatFormat.format("&8[&c&lDECLINE&8]")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan decline " + player.getName())));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> tabs = ClassicDupe.getOnlinePlayerUsernames();
        tabs.add("confirm");
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], tabs);
        return new ArrayList<>();
    }

    public static class InviteTask extends BukkitRunnable {
        @Override
        public void run() {
            for (Invite invite : invites) {
                if((invite.inviteSent + (1000*60)) < System.currentTimeMillis()) {
                    invites.remove(invite);
                    OfflinePlayer inviter = Bukkit.getOfflinePlayer(invite.inviterUUID);
                    OfflinePlayer invitee = Bukkit.getOfflinePlayer(invite.inviteeUUID);
                    if(inviter.isOnline()) Bukkit.getPlayer(invite.inviterUUID).sendMessage(Utils.cmdMsg("&eInvite sent to &6" + invitee.getName() + " &ehas expired"));
                    if(invitee.isOnline()) Bukkit.getPlayer(invite.inviteeUUID).sendMessage(Utils.cmdMsg("&eInvite from &6" + invitee + "&e has expired"));
                }
            }
        }
    }

    public static class Invite {
        public UUID inviterUUID;
        public UUID inviteeUUID;
        public long inviteSent;
        public Invite(UUID inviter, UUID invitee) {
            this.inviterUUID = inviter;
            this.inviteeUUID = invitee;
            this.inviteSent = System.currentTimeMillis();
        }
    }
}
