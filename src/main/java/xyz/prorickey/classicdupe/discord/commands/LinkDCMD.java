package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.commands.default1.LinkCMD;
import xyz.prorickey.classicdupe.discord.LinkRewards;

public class LinkDCMD {

    public static void execute(SlashCommandInteractionEvent inter) {
        if(ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromId(inter.getUser().getIdLong()) != null) {
            inter.reply("You must unlink your account with /unlink before you can link again").setEphemeral(true).queue();
            return;
        }
        String code = inter.getOption("code", OptionMapping::getAsString);
        if(!LinkCMD.linkCodes.containsKey(code)) {
            inter.reply("That code has expired. Execute /link in game again").setEphemeral(true).queue();
            return;
        }
        LinkCMD.LinkCode linkCode = LinkCMD.linkCodes.get(code);
        ClassicDupe.getDatabase().getLinkingDatabase().setLink(linkCode.player.getUniqueId().toString(), inter.getUser().getIdLong());
        inter.getGuild().addRoleToMember(inter.getUser(), inter.getGuild().getRoleById(1078109485144473620L)).queue();
        inter.reply("Successfully linked your account to " + linkCode.player.getName()).setEphemeral(true).queue();
        LinkCMD.linkCodes.remove(code);

        LinkRewards.checkRewardsForLinking(linkCode.player);
        LinkRewards.checkRewardsForBoosting(linkCode.player);
    }

}
