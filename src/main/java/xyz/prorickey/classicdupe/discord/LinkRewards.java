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
                        .getUser(player.getUniqueId()).data().add(Node.builder("perks.nickname").build());
                player.sendMessage(Utils.cmdMsg("&eThank you for linking your discord. You have recieved the nickname perk."));
            }
        }
    }

    public static void checkRewardsForBoosting(Player player) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if(link != null && ClassicDupeBot.getJDA().getGuildById(Config.getConfig().getLong("discord.guildId")).getMember(User.fromId(link.id)).isBoosting()) {
            if(ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().equals("default")) {
                ClassicDupe
                        .getLPAPI().getUserManager()
                        .getUser(player.getUniqueId()).data().add(
                                Node.builder("group.vip").build()
                        );
                player.sendMessage(Utils.cmdMsg("&eThank you for boosting the discord. You have recieved the VIP rank."));
            }
        }
    }

}
