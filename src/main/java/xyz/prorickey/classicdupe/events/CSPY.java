package xyz.prorickey.classicdupe.events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.moderator.CspyCMD;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

public class CSPY implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        CspyCMD.cspyList.forEach(p -> p.sendMessage(
                Utils.format("<dark_gray>[<yellow>CSPY<dark_gray>] <gold>" +
                        e.getPlayer().getName() +
                        " <yellow>executed <gold>" +
                        e.getMessage()
                )
        ));
        ClassicDupeBot.getJDA().getChannelById(TextChannel.class, Config.getConfig().getLong("discord.cspy"))
                .sendMessage("**" + e.getPlayer().getName() + "** executed `" + e.getMessage() + "`").queue();
    }

}
