package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.database.LinkingDatabase;

import java.util.UUID;

public class UnlinkDCMD {

    public static void execute(SlashCommandInteractionEvent inter) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromId(inter.getUser().getIdLong());
        if(link == null) {
            inter.reply("Your account is not linked").setEphemeral(true).queue();
            return;
        }
        ClassicDupe.getDatabase().getLinkingDatabase().unlinkById(inter.getUser().getIdLong());
        inter.getGuild().removeRoleFromMember(inter.getUser(), inter.getGuild().getRoleById(1078109485144473620L)).queue();
        inter.reply("Unlinked your account from " + Bukkit.getOfflinePlayer(UUID.fromString(link.uuid)).getName()).setEphemeral(true).queue();
    }

}
