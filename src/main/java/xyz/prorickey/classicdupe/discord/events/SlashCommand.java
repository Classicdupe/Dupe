package xyz.prorickey.classicdupe.discord.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.prorickey.classicdupe.discord.commands.StatsDCMD;

public class SlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "stats": StatsDCMD.execute(event);
        }
    }

}
