package xyz.prorickey.classicdupe;

import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.classicdupe.metrics.Metrics;
import xyz.prorickey.proutils.ChatFormat;

public class Scoreboard {

    public static class ScoreboardTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                scoreboard(player);
            });
        }
    }

    private static void scoreboard(Player player) {
        org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective(player.getUniqueId().toString()) == null ?
                board.registerNewObjective(player.getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                board.getObjective(player.getUniqueId().toString());

        if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.displayName(Component.text(ChatFormat.format("&a&lClassicDupe")));

        String clanName = ClanDatabase.getClanMember(player.getUniqueId()).getClanName();
        String clanColor = "&e";
        if(clanName != null) clanColor = ClanDatabase.getClan(ClanDatabase.getClanMember(player.getUniqueId()).getClanID()).getClanColor();

        updateScore(obj, 15, ChatFormat.format("&0&6&m----------------------"));

        updateScore(obj, 14, ChatFormat.format("&6\u2022 &eName &a" + player.getName()));
        updateScore(obj, 13, ChatFormat.format("&6\u2022 &eClan " + (clanName != null ? "&8[" + clanColor + clanName + "&8]" : "&eNo Clan")));
        updateScore(obj, 12, ChatFormat.format(("&6\u2022 &eRank " + (Utils.getPrefix(player).equals("") ? "&7Default" : Utils.getPrefix(player)))));
        updateScore(obj, 11, ChatFormat.format("&6\u2022 &eSuffix " + (Utils.getSuffix(player) != null ? Utils.getSuffix(player) : "&bUnset")));
        updateScore(obj, 10, ChatFormat.format("&6\u2022 &ePing &a" + player.getPing() + "ms"));

        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(player.getUniqueId().toString());

        updateScore(obj, 9, ChatFormat.format("&6\u2022 &eKills &a" + stats.kills));
        updateScore(obj, 8, ChatFormat.format("&6\u2022 &eDeaths &a" + stats.deaths));
        updateScore(obj, 7, ChatFormat.format("&6\u2022 &eKDR &a" + stats.kdr));

        updateScore(obj, 6, " ");

        // Server Stats

        if(Combat.inCombat.containsKey(player)) {
            // Combat Stats

            PlayerDatabase.PlayerStats menaceStats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(Combat.whoHitWho.get(player).getUniqueId().toString());

            long timeLeft = 15-(Math.round((System.currentTimeMillis()-Combat.inCombat.get(player))/1000));

            updateScore(obj, 5, ChatFormat.format("&6\u2022 &cFighting &e" + Combat.whoHitWho.get(player).getName()));
            updateScore(obj, 4, ChatFormat.format("&6\u2022 &cStats &e" + menaceStats.kills + "K " + menaceStats.deaths + "D"));
            updateScore(obj, 3, ChatFormat.format("&6\u2022 &cKDR &e" + menaceStats.kdr));
            updateScore(obj, 2, ChatFormat.format("&6\u2022 &cTimer &e" + timeLeft));
        } else {
            // Server Stats

            long tps = Math.round(SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.MINUTES_5));
            String tpsStr;
            if(tps > 18) tpsStr = "&a" + tps;
            else if(tps > 12) tpsStr = "&e" + tps;
            else tpsStr = "&c" + tps;

            updateScore(obj, 5, ChatFormat.format("&6\u2022 &eTPS " + tpsStr));
            updateScore(obj, 4, ChatFormat.format("&6\u2022 &eOnline &a" + Bukkit.getOnlinePlayers().size()));
            updateScore(obj, 3, ChatFormat.format("&6\u2022 &eUptime &a" + Metrics.getServerMetrics().getServerUptimeFormatted()));
            updateScore(obj, 2, ChatFormat.format("&6\u2022 &ePlaytime &a" + Metrics.getPlayerMetrics().getPlaytimeFormatted(player.getUniqueId())));
        }

        updateScore(obj, 1, ChatFormat.format("&1&6&m----------------------"));

        player.setScoreboard(board);
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

}
