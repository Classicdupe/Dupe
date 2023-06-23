package xyz.prorickey.classicdupe.metrics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMetrics implements Listener {

    private Connection conn;

    public PlayerMetrics(JavaPlugin plugin) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "metrics" + File.separator + "player");
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS playtime(uuid VARCHAR, alltime INT, season INT)").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new PlayerMetrics.PlaytimeTask().runTaskTimer(plugin, 0, 20);
    }

    @Nullable
    public Long getPlaytime(UUID uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM playtime WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) return (long) set.getInt("alltime");
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String getPlaytimeFormatted(UUID uuid) {
        Long playtime = getPlaytime(uuid);
        if(playtime == null) return null;
        Duration duration = Duration.ofMillis(playtime);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public static String getPlaytimeFormatted(Long playtime) {
        Duration duration = Duration.ofMillis(playtime);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public class PlaytimeTask extends BukkitRunnable {

        private static final Map<Player, Long> lastUpdate = new HashMap<>();

        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(!lastUpdate.containsKey(p)) lastUpdate.put(p, System.currentTimeMillis());
                Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                    try {
                        if(getPlaytime(p.getUniqueId()) == null) conn.prepareStatement("INSERT INTO playtime(uuid, alltime, season) VALUES('" + p.getUniqueId() + "', 0, 0)").execute();
                        long timeToLog = System.currentTimeMillis() - lastUpdate.get(p);
                        lastUpdate.put(p, System.currentTimeMillis());
                        conn.prepareStatement("UPDATE playtime SET alltime=alltime+" + timeToLog + ", season=season+" + timeToLog + " WHERE uuid='" + p.getUniqueId() + "'").execute();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                });
            });
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    ResultSet playtimeSet = conn.prepareStatement("SELECT * FROM playtime ORDER BY season DESC").executeQuery();
                    for(int i = 0; i < 10; i++) {
                        if(playtimeSet.next()) {
                            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(UUID.fromString(playtimeSet.getString("uuid")));
                            PlayerDatabase.playtimeLeaderboard.put(i+1, data.name);
                            PlayerDatabase.playtimeLeaderboardP.put(i+1, playtimeSet.getLong("season"));
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

}
