package xyz.prorickey.classicdupe;

import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.classicdupe.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard {

    private static final Map<Player, org.bukkit.scoreboard.Scoreboard> scoreboards = new HashMap<>();

    public static class ScoreboardTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                if(!scoreboards.containsKey(player)) {
                    org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                    scoreboards.put(player, board);
                    player.setScoreboard(board);
                }
                scoreboard(player, scoreboards.get(player));
            });
        }
    }

    private static void scoreboard(Player player, org.bukkit.scoreboard.Scoreboard board) {
        Objective obj = board.getObjective(player.getUniqueId().toString()) == null ?
                board.registerNewObjective(player.getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                board.getObjective(player.getUniqueId().toString());

        String displayName = "<green><bold>ClassicDupe";
        if(Utils.isVanished(player)) displayName = "<green><bold>ClassicDupe <red><b>(VANISHED)";
        if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        if(!obj.displayName().equals(Utils.format(displayName))) obj.displayName(Utils.format(displayName));

        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        String clanColor = "<yellow>";
        if(cmem.getClanName() != null) clanColor = ClassicDupe.getClanDatabase().getClan(ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).getClanID()).getClanColor();

        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
        if(data == null) return;

        updateTeamScore(obj, board, 15, Utils.format("<gold><st>\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"));
        updateTeamScore(obj, board, 14, Utils.format("<gold>\u2022 <yellow>Name <green>" + player.getName()));
        updateTeamScore(obj, board, 13, Utils.format("<gold>\u2022 <yellow>Clan " + (cmem.getClanName() != null ? "<dark_gray>[" + clanColor + cmem.getClanName() + "<dark_gray>]" : "<yellow>No Clan")));
        updateTeamScore(obj, board, 12, MiniMessage.miniMessage().deserialize(("<gold>\u2022 <yellow>Rank " + (Utils.getPrefix(player).equals("") ? "<gray>Default" : Utils.getPrefix(player)))));
        updateTeamScore(obj, board, 11, Utils.format("<gold>\u2022 <yellow>Suffix " + (Utils.getSuffix(player) != null ? Utils.convertColorCodesToAdventure(Utils.getSuffix(player)) : "<aqua>Unset")));
        updateTeamScore(obj, board, 10, Utils.format("<gold>\u2022 <yellow>Balance <green>" + data.balance + "DB"));

        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(player.getUniqueId().toString());

        updateTeamScore(obj, board, 9, Utils.format("<gold>\u2022 <yellow>Kills <green>" + stats.kills));
        updateTeamScore(obj, board, 8, Utils.format("<gold>\u2022 <yellow>Deaths <green>" + stats.deaths));
        updateTeamScore(obj, board, 7, Utils.format("<gold>\u2022 <yellow>KDR <green>" + stats.kdr));
        updateTeamScore(obj, board, 6, Component.text(" "));

        // Server Stats
        if(Combat.inCombat.containsKey(player) && Combat.whoHitWho.get(player) != null) {
            // Combat Stats

            PlayerDatabase.PlayerStats menaceStats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(Combat.whoHitWho.get(player).getUniqueId().toString());

            long timeLeft = 15-(Math.round((System.currentTimeMillis()-Combat.inCombat.get(player))/1000.0));

            updateTeamScore(obj, board, 5, Utils.format("<gold>\u2022 <red>Fighting <yellow>" + Combat.whoHitWho.get(player).getName()));
            updateTeamScore(obj, board, 4, Utils.format("<gold>\u2022 <red>Stats <yellow>" + menaceStats.kills + "K " + menaceStats.deaths + "D"));
            updateTeamScore(obj, board, 3, Utils.format("<gold>\u2022 <red>KDR <yellow>" + menaceStats.kdr));
            updateTeamScore(obj, board, 2, Utils.format("<gold>\u2022 <red>Timer <yellow>" + timeLeft));
        } else {
            // Server Stats

            long tps = Math.round(SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.MINUTES_5));
            String tpsStr;
            if(tps > 18) tpsStr = "<green>" + tps;
            else if(tps > 12) tpsStr = "<yellow>" + tps;
            else tpsStr = "<red>" + tps;

            updateTeamScore(obj, board, 5, Utils.format("<gold>\u2022 <yellow>TPS " + tpsStr));
            updateTeamScore(obj, board, 4, Utils.format("<gold>\u2022 <yellow>Online <green>" + ClassicDupe.getOnlinePlayerUsernames().size()));
            updateTeamScore(obj, board, 3, Utils.format("<gold>\u2022 <yellow>Uptime <green>" + Metrics.getServerMetrics().getServerUptimeFormatted()));
            updateTeamScore(obj, board, 2, Utils.format("<gold>\u2022 <yellow>Playtime <green>" + Metrics.getPlayerMetrics().getPlaytimeFormatted(player.getUniqueId())));
        }

        updateTeamScore(obj, board, 1, Utils.format("<gold><st>\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"));
    }

    private static void updateTeamScore(Objective obj, org.bukkit.scoreboard.Scoreboard board, int score, Component value) {
        String color = colors.get(score);
        Team line = board.getTeam(color) == null ?
                board.registerNewTeam(color) :
                board.getTeam(color);
        line.addEntry(color);
        line.prefix(value);
        obj.getScore(color).setScore(score);
    }

    private static void updateScore(Objective obj, int score, String name) {
        for (String s : obj.getScoreboard().getEntries().stream().toList()) {
            if(obj.getScore(s).getScore() == score && s.equals(name)) return;
            if(obj.getScore(s).getScore() == score) {
                obj.getScore(s).resetScore();
                obj.getScore(name).setScore(score);
            }
        }
        obj.getScore(name).setScore(score);
    }

    private static final Map<Integer, String> colors = new HashMap<>(){{
        put(15, "\u00A70");
        put(14, "\u00A71");
        put(13, "\u00A72");
        put(12, "\u00A73");
        put(11, "\u00A74");
        put(10, "\u00A75");
        put(9, "\u00A76");
        put(8, "\u00A77");
        put(7, "\u00A78");
        put(6, "\u00A79");
        put(5, "\u00A7a");
        put(4, "\u00A7b");
        put(3, "\u00A7c");
        put(2, "\u00A7d");
        put(1, "\u00A7e");
    }};

}
