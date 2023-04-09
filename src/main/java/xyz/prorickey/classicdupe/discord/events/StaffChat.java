package xyz.prorickey.classicdupe.discord.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.proutils.ChatFormat;

public class StaffChat extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(Config.getConfig().getLong("discord.staffchat") != event.getChannel().getIdLong() || event.getAuthor().isBot()) return;

        StaffChatCMD.sendToStaffChat(
                "&8[&cSC&bDSC&8] &e" +
                        event.getAuthor().getName() +
                        ChatFormat.format(" &7\u00BB &a") +
                        event.getMessage().getContentRaw()
        );
    }
}
