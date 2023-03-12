package xyz.prorickey.classicdupe.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.discord.events.SlashCommand;

public class ClassicDupeBot extends ListenerAdapter {

    ClassicDupe plugin;
    public JDA jda;

    public ClassicDupeBot(ClassicDupe p) {
        plugin = p;
        jda = JDABuilder
                .createDefault(Config.getConfig().getString("discord.token"))
                .addEventListeners(new SlashCommand())
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("stats", "Get the stats of a player")
                        .addOption(OptionType.STRING, "username", "The username of the player you want the stats of", true),
                Commands.slash("execute", "To execute a console command")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOption(OptionType.STRING, "command", "The command you would like to execute", true),
                Commands.slash("link", "To link your minecraft account")
                        .addOption(OptionType.STRING, "code", "The link code you were provided", true)

        ).queue();

        ServerStatsUpdater task = new ServerStatsUpdater();
        task.runTaskTimer(plugin, 0, 20*60*5);
    }

    public class ServerStatsUpdater extends BukkitRunnable {
        @Override
        public void run() {
            jda.getVoiceChannelById(Config.getConfig().getLong("discord.onlineplayers"))
                    .getManager()
                    .setName("\uD83D\uDCC8\u30FBPlayers Online: " + plugin.getServer().getOnlinePlayers().size())
                    .queue();
        }
    }

}
