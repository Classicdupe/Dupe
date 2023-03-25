package xyz.prorickey.classicdupe.metrics;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;

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
    private JavaPlugin plugin;

    public PlayerMetrics(JavaPlugin plugin) {
        this.plugin = plugin;
        try {
            conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "metrics" + File.separator + "player");
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS playtime(uuid VARCHAR, alltime INT, season INT)").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

    private Map<String, BukkitTask> tasks = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(getPlaytime(e.getPlayer().getUniqueId()) == null) {
            try {
                conn.prepareStatement("INSERT INTO playtime(uuid, alltime, season) VALUES('" + e.getPlayer().getUniqueId() + "', 0, 0)").execute();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        final Long[] lastUpdate = {System.currentTimeMillis()};
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                long timeToLog = System.currentTimeMillis()- lastUpdate[0];
                lastUpdate[0] = System.currentTimeMillis();
                try {
                    conn.prepareStatement("UPDATE playtime SET alltime=alltime+" + timeToLog + ", season=season+" + timeToLog + " WHERE uuid='" + e.getPlayer().getUniqueId() + "'").execute();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }.runTaskTimer(this.plugin, 0, 10);
        tasks.put(e.getPlayer().getUniqueId().toString(), task);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        tasks.remove(e.getPlayer().getUniqueId().toString());
    }

}
