package xyz.prorickey.classicdupe.events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.commands.moderator.CspyCMD;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;
import xyz.prorickey.proutils.ChatFormat;

public class CSPY implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        CspyCMD.cspyList.forEach(p -> p.sendMessage(ChatFormat.format(
                "&8[&eCSPY&8] &6" +
                        e.getPlayer().getName() +
                        " &eexecuted &6" +
                        e.getMessage()
        )));
        ClassicDupeBot.getJDA().getChannelById(TextChannel.class, Config.getConfig().getLong("discord.cspy"))
                .sendMessage("**" + e.getPlayer().getName() + "** executed `" + e.getMessage() + "`").queue();
    }

}
