package xyz.prorickey.classicdupe.discord;

import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.LinkingDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

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

    public static boolean checkRewardsForBoosting(Player player) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if(link == null) return false;
        AtomicBoolean isBoosting = new AtomicBoolean(false);
        ClassicDupeBot.getJDA().getGuildById(Config.getConfig().getLong("discord.guild")).retrieveMemberById(link.id).queue(member -> {
            if(member.isBoosting()) {
                isBoosting.set(true);
                if(ClassicDupe.getLPAPI().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().equals("default")) {
                    ClassicDupe
                            .getLPAPI().getUserManager()
                            .getUser(player.getUniqueId()).data().add(
                                    Node.builder("group.booster").build()
                            );
                    player.sendMessage(Utils.cmdMsg("<yellow>Thank you for boosting the discord. You have recieved the VIP rank."));
                }
            }
        });

        return isBoosting.get();
    }

}
