package xyz.prorickey.classicdupe;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.proutils.ChatFormat;

import java.util.List;

public class Scoreboard {

    public static class ScoreboardTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getServer().getOnlinePlayers().forEach(Scoreboard::scoreboard);
        }
    }

    private static void scoreboard(Player player) {
        org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective(player.getUniqueId().toString()) == null ?
                board.registerNewObjective(player.getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                board.getObjective(player.getUniqueId().toString());

        if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.displayName(Component.text(ChatFormat.format("&a&lClassicDupe")));

        List<String> scores = Config.getConfig().getStringList("scoreboard");
        for(int i = 0; i < 15; i++) replaceScore(obj, 15-i, ChatFormat.format(scores.get(i)));

        replaceScore(obj, 15, ChatFormat.format("&0&6&m----------------------"));

        replaceScore(obj, 14, ChatFormat.format("&6\u2022 &eName &a" + player.getName()));
        replaceScore(obj, 13, ChatFormat.format("&6\u2022 &eClan &aComingSoon"));
        replaceScore(obj, 12, ChatFormat.format("&6\u2022 &eRank " + Utils.getPrefix(player)));
        replaceScore(obj, 11, ChatFormat.format("&6\u2022 &eSuffix " + Utils.getSuffix(player)));
        replaceScore(obj, 10, ChatFormat.format("&6\u2022 &ePing &e" + player.getPing() + "ms"));

        PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(player.getUniqueId().toString());

        replaceScore(obj, 9, ChatFormat.format("&6\u2022 &eKills &a" + stats.kills));
        replaceScore(obj, 8, ChatFormat.format("&6\u2022 &eDeaths &a" + stats.deaths));
        replaceScore(obj, 7, ChatFormat.format("&6\u2022 &eKDR &a" + stats.kdr));

        replaceScore(obj, 6, " ");

        // Server Stats

        String tpsStr;
        if(ClassicDupe.tps > 18) tpsStr = "&a" + ClassicDupe.tps;
        else if(ClassicDupe.tps > 12) tpsStr = "&e" + ClassicDupe.tps;
        else tpsStr = "&c" + ClassicDupe.tps;

        replaceScore(obj, 5, ChatFormat.format("&6\u2022 &eTPS " + tpsStr));
        replaceScore(obj, 4, ChatFormat.format("&6\u2022 &eOnline &a" + Bukkit.getOnlinePlayers().size()));
        replaceScore(obj, 3, ChatFormat.format("&6\u2022 &eUptime &a20 Hours"));
        replaceScore(obj, 2, ChatFormat.format("&6\u2022 &ePlaytime &a4d 20h 5m"));

        replaceScore(obj, 1, ChatFormat.format("&1&6&m----------------------"));

        player.setScoreboard(board);
    }

    private static void replaceScore(Objective obj, int score, String name) {
        if(hasScoreTaken(obj, score)) {
            if(getEntryFromScore(obj, score).equalsIgnoreCase(name)) return;
            if(!(getEntryFromScore(obj, score).equalsIgnoreCase(name))) obj.getScoreboard().resetScores(getEntryFromScore(obj, score));
        }
        obj.getScore(name).setScore(score);
    }

    private static boolean hasScoreTaken(Objective obj, int score) {
        for (String s : obj.getScoreboard().getEntries()) if(obj.getScore(s).getScore() == score) return true;
        return false;
    }

    private static String getEntryFromScore(Objective obj, int score) {
        if(obj == null) return null;
        if(!hasScoreTaken(obj, score)) return null;
        for (String s : obj.getScoreboard().getEntries()) if(obj.getScore(s).getScore() == score) return obj.getScore(s).getEntry();
        return null;
    }

}
