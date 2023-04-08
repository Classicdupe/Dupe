package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.database.LinkingDatabase;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.metrics.Metrics;

import java.util.UUID;

public class StatsDCMD {

    public static void execute(SlashCommandInteractionEvent interaction) {
        String username = interaction.getOption("username", OptionMapping::getAsString);
        PlayerDatabase.PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(Bukkit.getOfflinePlayer(username).getUniqueId().toString());
        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(Bukkit.getOfflinePlayer(username).getUniqueId().toString());
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(Bukkit.getOfflinePlayer(username).getUniqueId().toString());
        String clanName = ClanDatabase.getClanMember(UUID.fromString(data.uuid)).getClanName();
        if(data == null || stats == null) {
            interaction.reply("That player isn't in our database").setEphemeral(true).queue();
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(data.name + "'s Stats");
        StringBuilder message = new StringBuilder();
        message.append("Nickname: ").append(data.nickname != null ? data.nickname : "None").append("\n")
                .append("Clan: ").append(clanName != null ? clanName : "No Clan").append("\n")
                .append("Kills: ").append(stats.kills).append("\n")
                .append("Deaths: ").append(stats.deaths).append("\n")
                .append("KDR: ").append(stats.kdr).append("\n")
                .append("Playtime: ").append(Metrics.getPlayerMetrics().getPlaytimeFormatted(UUID.fromString(data.uuid))).append("\n");
        if(link != null) message.append("Linked: ").append("<@").append(link.id).append(">");
        builder.setDescription(message.toString());
        builder.setColor(0xFF4646);
        builder.setThumbnail("http://cravatar.eu/helmavatar/" + Bukkit.getOfflinePlayer(username).getUniqueId());
        interaction.replyEmbeds(builder.build()).queue();
    }

}
