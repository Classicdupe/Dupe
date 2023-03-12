package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;

public class ExecuteDCMD {

    public static void execute(SlashCommandInteractionEvent inter) {
        if(!inter.getMember().getRoles().contains(inter.getGuild().getRoleById(1068991520029552721L))) {
            inter.reply("You cannot execute this command").setEphemeral(true).queue();
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), inter.getOption("command", OptionMapping::getAsString));
        inter.reply("Executed the command `" + inter.getOption("command", OptionMapping::getAsString) + "`").setEphemeral(true).queue();
    }

}
