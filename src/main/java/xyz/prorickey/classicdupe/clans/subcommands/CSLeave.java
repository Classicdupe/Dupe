package xyz.prorickey.classicdupe.clans.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;

import java.util.ArrayList;
import java.util.List;

public class CSLeave extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou aren't in a clan"));
            return;
        }
        if(cmem.getLevel() == 4) {
            player.sendMessage(Utils.cmdMsg("&cYou cannot leave a clan that you own. You must promote someone else to owner before you can leave"));
            return;
        }
        cmem.removeClan();
        player.sendMessage(Utils.cmdMsg("&eYou have left your clan"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
