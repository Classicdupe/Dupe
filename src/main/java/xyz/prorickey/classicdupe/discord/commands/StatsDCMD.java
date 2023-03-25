package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.metrics.Metrics;

import java.util.UUID;

public class StatsDCMD {

    public static void execute(SlashCommandInteractionEvent interaction) {
        String username = interaction.getOption("username", OptionMapping::getAsString);
        PlayerDatabase.PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(Bukkit.getOfflinePlayer(username).getUniqueId().toString());
        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(Bukkit.getOfflinePlayer(username).getUniqueId().toString());
        String clanName = ClansDatabase.getClanMember(UUID.fromString(data.uuid)).getClanName();
        if(data == null || stats == null) {
            interaction.reply("That player isn't in our database").setEphemeral(true).queue();
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(data.name + "'s Stats");
        builder.setDescription("Nickname: " + (data.nickname != null ? data.nickname : "None") + "\n" +
                "Clan: " + (clanName != null ? clanName : "No Clan") + "\n" +
                "Kills: " + stats.kills + "\n" +
                "Deaths: " + stats.deaths + "\n" +
                "KDR: " + stats.kdr + "\n" +
                "Playtime: " + Metrics.getPlayerMetrics().getPlaytimeFormatted(UUID.fromString(data.uuid)));
        builder.setColor(0xFF4646);
        builder.setThumbnail("http://cravatar.eu/helmavatar/" + Bukkit.getOfflinePlayer(username).getUniqueId());
        interaction.replyEmbeds(builder.build()).queue();
    }

}
