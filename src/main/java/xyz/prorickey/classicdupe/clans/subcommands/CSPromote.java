package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.Clan;
import xyz.prorickey.classicdupe.clans.ClanMember;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class CSPromote extends ClanSub {

    private static Map<UUID, OfflinePlayer> promoterToPromotee = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to promote players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a player to promote"));
            return;
        }
        Clan clan = ClanDatabase.getClan(cmem.getClanID());
        if(args[0].equalsIgnoreCase("confirm")) {
            if(!promoterToPromotee.containsKey(player.getUniqueId())) {
                player.sendMessage(Utils.cmdMsg("&cYou must first promote the player with the regular promote command before confirming"));
                promoterToPromotee.remove(player.getUniqueId());
                return;
            }
            OfflinePlayer offPlayer = promoterToPromotee.get(player.getUniqueId());
            ClanMember pmem = ClanDatabase.getClanMember(offPlayer.getUniqueId());
            if(!Objects.equals(pmem.getClanID(), cmem.getClanID())) {
                player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
                promoterToPromotee.remove(player.getUniqueId());
                return;
            }
            ClanDatabase.setPlayerLevel(cmem.getOffPlayer().getUniqueId(), 2);
            cmem.setLevel(2);
            ClanDatabase.setPlayerLevel(pmem.getOffPlayer().getUniqueId(), 3);
            pmem.setLevel(3);
            player.sendMessage(Utils.cmdMsg("&6" + offPlayer.getName() + "&e has been promoted to owner and you have been demoted to admin"));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been promoted to owner of your clan by &6" + player.getName()));
            promoterToPromotee.remove(player.getUniqueId());
            return;
        }
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!clan.getMembers().contains(offPlayer)) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        ClanMember pmem = ClanDatabase.getClanMember(offPlayer.getUniqueId());
        if(!Objects.equals(pmem.getClanID(), cmem.getClanID())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        if(pmem.getLevel() < 1) {
            if(cmem.getLevel() < 2) {
                player.sendMessage(Utils.cmdMsg("&cYou must be at least an admin to promote players"));
                return;
            }
            ClanDatabase.setPlayerLevel(pmem.getOffPlayer().getUniqueId(), pmem.getLevel()+1);
            pmem.setLevel(pmem.getLevel()+1);
            player.sendMessage(Utils.cmdMsg("&ePromoted &6" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been promoted in your clan by &6" + player.getName()));
        } else if(pmem.getLevel() == 1) {
            if(cmem.getLevel() < 3) {
                player.sendMessage(Utils.cmdMsg("&cYou must be the owner of the clan to promote people to admin"));
                return;
            }
            ClanDatabase.setPlayerLevel(pmem.getOffPlayer().getUniqueId(), 2);
            pmem.setLevel(2);
            player.sendMessage(Utils.cmdMsg("&ePromoted &6" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been promoted in your clan by &6" + player.getName()));
        } else if(pmem.getLevel() == 2) {
            if(cmem.getLevel() != 3) {
                player.sendMessage(Utils.cmdMsg("&cYou must be the owner to transfer ownership of the clan to someone"));
                return;
            }
            player.sendMessage(Component.text(Utils.cmdMsg("&eYou are about to transfer ownership of your clan to &6" + offPlayer.getName() + "&e. Are you sure you want to do this? "))
                    .append(Component.text(ChatFormat.format("&8[&a&lYES&8]"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan promote confirm"))));
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
