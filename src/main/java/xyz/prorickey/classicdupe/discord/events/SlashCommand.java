package xyz.prorickey.classicdupe.discord.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.prorickey.classicdupe.discord.commands.ExecuteDCMD;
import xyz.prorickey.classicdupe.discord.commands.LinkDCMD;
import xyz.prorickey.classicdupe.discord.commands.UnlinkDCMD;

public class SlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "execute" -> ExecuteDCMD.execute(event);
            case "link" -> LinkDCMD.execute(event);
            case "unlink" -> UnlinkDCMD.execute(event);
        }
    }

}