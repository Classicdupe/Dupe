package xyz.prorickey.classicdupe;

import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.ClanMember;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.classicdupe.metrics.Metrics;
import xyz.prorickey.proutils.ChatFormat;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard {

    private static Map<Player, org.bukkit.scoreboard.Scoreboard> scoreboards = new HashMap<>();

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

        if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        if(!obj.displayName().equals(Component.text(ChatFormat.format("&a&lClassicDupe")))) obj.displayName(Component.text(ChatFormat.format("&a&lClassicDupe")));

        ClanMember cmem = ClanDatabase.getClanMember(player.getUniqueId());
        String clanColor = "&e";
        if(cmem.getClanName() != null) clanColor = ClanDatabase.getClan(ClanDatabase.getClanMember(player.getUniqueId()).getClanID()).getClanColor();

        updateTeamScore(obj, board, 15, "&0&6&m----------------------");
        updateTeamScore(obj, board, 14, "&6\u2022 &eName &a" + player.getName());
        updateTeamScore(obj, board, 13, "&6\u2022 &eClan " + (cmem.getClanName() != null ? "&8[" + clanColor + cmem.getClanName() + "&8]" : "&eNo Clan"));
        updateTeamScore(obj, board, 12, ("&6\u2022 &eRank " + (Utils.getPrefix(player).equals("") ? "&7Default" : Utils.getPrefix(player))));
        updateTeamScore(obj, board, 11, "&6\u2022 &eSuffix " + (Utils.getSuffix(player) != null ? Utils.getSuffix(player) : "&bUnset"));
        updateTeamScore(obj, board, 10, "&6\u2022 &ePing &a" + player.getPing() + "ms");

        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(player.getUniqueId().toString());

        updateTeamScore(obj, board, 9, "&6\u2022 &eKills &a" + stats.kills);
        updateTeamScore(obj, board, 8, "&6\u2022 &eDeaths &a" + stats.deaths);
        updateTeamScore(obj, board, 7, "&6\u2022 &eKDR &a" + stats.kdr);
        updateTeamScore(obj, board, 6, " ");

        // Server Stats
        if(Combat.inCombat.containsKey(player) && Combat.whoHitWho.get(player) != null) {
            // Combat Stats

            PlayerDatabase.PlayerStats menaceStats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(Combat.whoHitWho.get(player).getUniqueId().toString());

            long timeLeft = 15-(Math.round((System.currentTimeMillis()-Combat.inCombat.get(player))/1000));

            updateTeamScore(obj, board, 5, "&6\u2022 &cFighting &e" + Combat.whoHitWho.get(player).getName());
            updateTeamScore(obj, board, 4, "&6\u2022 &cStats &e" + menaceStats.kills + "K " + menaceStats.deaths + "D");
            updateTeamScore(obj, board, 3, "&6\u2022 &cKDR &e" + menaceStats.kdr);
            updateTeamScore(obj, board, 2, "&6\u2022 &cTimer &e" + timeLeft);
        } else {
            // Server Stats

            long tps = Math.round(SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.MINUTES_5));
            String tpsStr;
            if(tps > 18) tpsStr = "&a" + tps;
            else if(tps > 12) tpsStr = "&e" + tps;
            else tpsStr = "&c" + tps;

            updateTeamScore(obj, board, 5, "&6\u2022 &eTPS " + tpsStr);
            updateTeamScore(obj, board, 4, "&6\u2022 &eOnline &a" + Bukkit.getOnlinePlayers().size());
            updateTeamScore(obj, board, 3, "&6\u2022 &eUptime &a" + Metrics.getServerMetrics().getServerUptimeFormatted());
            updateTeamScore(obj, board, 2, "&6\u2022 &ePlaytime &a" + Metrics.getPlayerMetrics().getPlaytimeFormatted(player.getUniqueId()));
        }

        updateTeamScore(obj, board, 1, "&1&6&m----------------------");
    }

    private static void updateTeamScore(Objective obj, org.bukkit.scoreboard.Scoreboard board, int score, String value) {
        String color = colors.get(score);
        Team line = board.getTeam(color) == null ?
                board.registerNewTeam(color) :
                board.getTeam(color);
        line.addEntry(color);
        line.prefix(Component.text(ChatFormat.format(value)));
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

    private static Map<Integer, String> colors = new HashMap<>(){{
        put(15, ChatColor.BLACK.toString());
        put(14, ChatColor.DARK_BLUE.toString());
        put(13, ChatColor.DARK_GREEN.toString());
        put(12, ChatColor.DARK_AQUA.toString());
        put(11, ChatColor.DARK_RED.toString());
        put(10, ChatColor.DARK_PURPLE.toString());
        put(9, ChatColor.GRAY.toString());
        put(8, ChatColor.GOLD.toString());
        put(7, ChatColor.DARK_GRAY.toString());
        put(6, ChatColor.BLUE.toString());
        put(5, ChatColor.GREEN.toString());
        put(4, ChatColor.AQUA.toString());
        put(3, ChatColor.RED.toString());
        put(2, ChatColor.LIGHT_PURPLE.toString());
        put(1, ChatColor.YELLOW.toString());
    }};

}
