package xyz.prorickey.classicdupe.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.discord.events.SlashCommand;
import xyz.prorickey.classicdupe.discord.events.StaffChat;

public class ClassicDupeBot extends ListenerAdapter {

    final ClassicDupe plugin;
    public static JDA jda;

    public ClassicDupeBot(ClassicDupe p) {
        plugin = p;
        jda = JDABuilder
                .createDefault(Config.getConfig().getString("discord.token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(
                        new SlashCommand(),
                        new StaffChat()
                )
                .build();
    }

    public static JDA getJDA() { return jda; }

}
