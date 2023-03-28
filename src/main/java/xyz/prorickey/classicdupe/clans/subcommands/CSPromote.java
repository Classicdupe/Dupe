package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;

import java.util.*;

public class CSPromote extends ClanSub {

    private static Map<UUID, OfflinePlayer> promoterToPromotee = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to promote players"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must provide a player to promote"));
            return;
        }
        ClansDatabase.Clan clan = ClansDatabase.getClanByID(cmem.getClanId());
        if(args[0].equalsIgnoreCase("confirm")) {
            if(!promoterToPromotee.containsKey(player.getUniqueId())) {
                player.sendMessage(Utils.cmdMsg("&cYou must first promote the player with the regular promote command before confirming"));
                promoterToPromotee.remove(player.getUniqueId());
                return;
            }
            OfflinePlayer offPlayer = promoterToPromotee.get(player.getUniqueId());
            ClansDatabase.ClanMember pmem = ClansDatabase.getClanMember(offPlayer.getUniqueId());
            if(!Objects.equals(pmem.getClanId(), cmem.getClanId())) {
                player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
                promoterToPromotee.remove(player.getUniqueId());
                return;
            }
            cmem.setLevel(3);
            pmem.setLevel(4);
            player.sendMessage(Utils.cmdMsg("&6" + offPlayer.getName() + "&e has been promoted to owner and you have been demoted to admin"));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been promoted to owner of your clan by &6" + player.getName()));
            promoterToPromotee.remove(player.getUniqueId());
            return;
        }
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!clan.getClanMemberUUIDs().contains(offPlayer.getUniqueId())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        ClansDatabase.ClanMember pmem = ClansDatabase.getClanMember(offPlayer.getUniqueId());
        if(!Objects.equals(pmem.getClanId(), cmem.getClanId())) {
            player.sendMessage(Utils.cmdMsg("&cThat player is not in your clan"));
            return;
        }
        if(pmem.getLevel() < 2) {
            if(cmem.getLevel() < 3) {
                player.sendMessage(Utils.cmdMsg("&cYou must be at least an admin to promote players"));
                return;
            }
            pmem.setLevel(pmem.getLevel()+1);
            player.sendMessage(Utils.cmdMsg("&ePromoted &6" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been promoted in your clan by &6" + player.getName()));
        } else if(pmem.getLevel() == 2) {
            if(cmem.getLevel() < 4) {
                player.sendMessage(Utils.cmdMsg("&cYou must be the owner of the clan to promote people to admin"));
                return;
            }
            pmem.setLevel(3);
            player.sendMessage(Utils.cmdMsg("&ePromoted &6" + pmem.getOffPlayer().getName()));
            if(offPlayer.isOnline()) offPlayer.getPlayer().sendMessage(Utils.cmdMsg("&eYou have been promoted in your clan by &6" + player.getName()));
        } else if(pmem.getLevel() == 3) {
            if(cmem.getLevel() != 4) {
                player.sendMessage(Utils.cmdMsg("&cYou must be the owner to transfer ownership of the clan to someone"));
                return;
            }
            player.sendMessage(Component.text(Utils.cmdMsg("&eYou are about to transfer ownership of your clan to &6" + offPlayer.getName() + "&e. Are you sure you want to do this?"))
                    .append(Component.text(ChatFormat.format("&8[&a&lYES&8]"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan promote confirm"))));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
