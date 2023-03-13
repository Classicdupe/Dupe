package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;

public class ExecuteDCMD {

    public static void execute(SlashCommandInteractionEvent inter) {
        if(!inter.getMember().getRoles().contains(inter.getGuild().getRoleById(1068991520029552721L))) {
            inter.reply("You cannot execute this command").setEphemeral(true).queue();
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ClassicDupe.executeConsoleCommand(inter.getOption("command", OptionMapping::getAsString));
            }
        }.runTask(ClassicDupe.getPlugin());
        inter.reply("Executed the command `" + inter.getOption("command", OptionMapping::getAsString) + "`").setEphemeral(true).queue();
    }

}
