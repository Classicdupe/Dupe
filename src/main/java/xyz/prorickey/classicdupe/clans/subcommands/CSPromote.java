package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.event.ClickEvent;
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

import java.util.*;

@SuppressWarnings("unused")
public class CSPromote extends ClanSub {

    private static final Map<UUID, OfflinePlayer> promoterToPromotee = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You must be in a clan to promote players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must provide a player to promote"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        if(args[0].equalsIgnoreCase("confirm")) {
            if(!promoterToPromotee.containsKey(player.getUniqueId())) {
                player.sendMessage(Utils.cmdMsg("<red>You must first promote the player with the regular promote command before confirming"));
                promoterToPromotee.remove(player.getUniqueId());
                return;
            }
            OfflinePlayer offPlayer = promoterToPromotee.get(player.getUniqueId());
            ClanMember pmem = ClassicDupe.getClanDatabase().getClanMember(offPlayer.getUniqueId());
            if(!Objects.equals(pmem.getClanID(), cmem.getClanID())) {
                player.sendMessage(Utils.cmdMsg("<red>That player is not in your clan"));
                promoterToPromotee.remove(player.getUniqueId());
                return;
            }
            ClassicDupe.getClanDatabase().setPlayerLevel(cmem, 2);
            cmem.setLevel(2);
            ClassicDupe.getClanDatabase().setPlayerLevel(pmem, 3);
            pmem.setLevel(3);
            clan.setOwner(pmem.getOffPlayer());
            clan.removeAdmin(pmem.getOffPlayer());
            clan.addAdmin(cmem.getOffPlayer());
            player.sendMessage(Utils.cmdMsg("<gold>" + offPlayer.getName() + "<yellow> has been promoted to owner and you have been demoted to admin"));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("<yellow>You have been promoted to owner of your clan by <gold>" + player.getName()));
            promoterToPromotee.remove(player.getUniqueId());
            return;
        }
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
        if(pmem.getLevel() < 1) {
            if(cmem.getLevel() < 2) {
                player.sendMessage(Utils.cmdMsg("<red>You must be at least an admin to promote players"));
                return;
            }
            ClassicDupe.getClanDatabase().setPlayerLevel(pmem, pmem.getLevel()+1);
            pmem.setLevel(pmem.getLevel()+1);
            clan.removeDefault(pmem.getOffPlayer());
            clan.addVip(pmem.getOffPlayer());
            player.sendMessage(Utils.cmdMsg("<yellow>Promoted <gold>" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("<yellow>You have been promoted in your clan by <gold>" + player.getName()));
        } else if(pmem.getLevel() == 1) {
            if(cmem.getLevel() < 3) {
                player.sendMessage(Utils.cmdMsg("<red>You must be the owner of the clan to promote people to admin"));
                return;
            }
            ClassicDupe.getClanDatabase().setPlayerLevel(pmem, 2);
            pmem.setLevel(2);
            clan.removeVip(pmem.getOffPlayer());
            clan.addAdmin(pmem.getOffPlayer());
            player.sendMessage(Utils.cmdMsg("<yellow>Promoted <gold>" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("<yellow>You have been promoted in your clan by <gold>" + player.getName()));
        } else if(pmem.getLevel() == 2) {
            if(cmem.getLevel() != 3) {
                player.sendMessage(Utils.cmdMsg("<red>You must be the owner to transfer ownership of the clan to someone"));
                return;
            }
            player.sendMessage(Utils.cmdMsg("<yellow>You are about to transfer ownership of your clan to <gold>" + offPlayer.getName() + "<yellow>. Are you sure you want to do this? ")
                    .append(Utils.format("<dark_gray>[<green><b>YES<dark_gray>]"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan promote confirm")));
            promoterToPromotee.put(player.getUniqueId(), offPlayer);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> tabs = ClassicDupe.getOnlinePlayerUsernames();
        tabs.add("confirm");
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], tabs);
        return new ArrayList<>();
    }
}
