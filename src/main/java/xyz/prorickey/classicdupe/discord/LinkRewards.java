package xyz.prorickey.classicdupe.discord;

import net.dv8tion.jda.api.entities.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.LinkingDatabase;

public class LinkRewards {

    public static void checkRewardsForLinking(Player player) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if(link != null) {
            if(!player.hasPermission("perks.nickname")) {
                ClassicDupe
                        .getLPAPI().getUserManager()
                        .getUser(player.getUniqueId()).data().add(Node.builder("perks.hat").build());
                player.sendMessage(Utils.cmdMsg("<yellow>Thank you for linking your discord. You have recieved the hat perk."));
            }
        }
    }

    public static void checkRewardsForBoosting(Player player) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if(link != null && ClassicDupeBot.getJDA().getGuildById(Config.getConfig().getLong("discord.guild")).getMemberById(link.id).isBoosting()) {
            if(ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().equals("default")) {
                ClassicDupe
                        .getLPAPI().getUserManager()
                        .getUser(player.getUniqueId()).data().add(
                                Node.builder("group.vip").build()
                        );
                player.sendMessage(Utils.cmdMsg("<yellow>Thank you for boosting the discord. You have recieved the VIP rank."));
            }
        }
    }

}
