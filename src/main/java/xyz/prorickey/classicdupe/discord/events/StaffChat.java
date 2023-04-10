package xyz.prorickey.classicdupe.discord.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;

public class StaffChat extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(Config.getConfig().getLong("discord.staffchat") != event.getChannel().getIdLong() || event.getAuthor().isBot()) return;

        StaffChatCMD.sendToStaffChat(
                Utils.format("<dark_gray>[<red>SC<aqua>DSC<dark_gray>] <yellow>" +
                        event.getAuthor().getName() +
                        " <gray>\u00BB <green> " +
                        event.getMessage().getContentRaw()));
    }
}
