package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.metrics.Metrics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerDatabase {

    final Connection conn;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerDatabase(Connection conn) {
        this.conn = conn;
        new BalanceTopTask().runTaskTimerAsynchronously(ClassicDupe.getPlugin(), 0, 20 * 60 * 5);
    }

    /**
     * Gets the player data of a player. Returns null if the player is not in the database
     * @param uuid UUID of the player
     * @return PlayerData of the player. Null if not in database
     */
    @Nullable
    public PlayerData getPlayerData(UUID uuid) {
        if(playerDataMap.containsKey(uuid)) return playerDataMap.get(uuid);
        try {
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM players WHERE uuid=?");
            stat.setString(1, uuid.toString());
            ResultSet set = stat.executeQuery();
            if(set.next()) {
                return new PlayerData(
                        conn,
                        UUID.fromString(set.getString("uuid")),
                        set.getString("name"),
                        set.getString("nickname"),
                        set.getLong("timesjoined"),
                        set.getLong("playtime"),
                        set.getBoolean("randomitem"),
                        set.getString("chatcolor"),
                        set.getBoolean("gradient"),
                        set.getString("gradientfrom"),
                        set.getString("gradientto"),
                        set.getBoolean("night"),
                        set.getInt("balance"),
                        set.getBoolean("deathmessages"),
                        set.getBoolean("mutepings"),
                        set.getInt("killStreak")
                );
            }
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

    public void playerDataUnload(UUID uuid) { playerDataMap.remove(uuid); }

    public void playerDataUpdateAndLoad(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("SELECT * FROM players WHERE uuid=?");
                stat.setString(1, player.getUniqueId().toString());
                ResultSet set = stat.executeQuery();
                if(set.next()) {
                    PreparedStatement stat1 = conn.prepareStatement("UPDATE players SET name=?, timesjoined=timesjoined+1 WHERE uuid=?");
                    stat1.setString(1, player.getName());
                    stat1.setString(2, player.getUniqueId().toString());
                    stat1.execute();
                    playerDataMap.put(player.getUniqueId(), new PlayerData(
                            conn,
                            UUID.fromString(set.getString("uuid")),
                            set.getString("name"),
                            set.getString("nickname"),
                            set.getLong("timesjoined"),
                            set.getLong("playtime"),
                            set.getBoolean("randomitem"),
                            set.getString("chatcolor"),
                            set.getBoolean("gradient"),
                            set.getString("gradientfrom"),
                            set.getString("gradientto"),
                            set.getBoolean("night"),
                            set.getInt("balance"),
                            set.getBoolean("deathmessages"),
                            set.getBoolean("mutepings"),
                            set.getInt("killStreak")
                    ));
                } else {
                    PreparedStatement stat1 = conn.prepareStatement("INSERT INTO players(uuid, name, nickname, timesjoined, playtime, randomitem, chatcolor, gradient, gradientfrom, gradientto, night, balance, deathmessages, mutepings) VALUES (?, ?, null, 1, 0, true, '<gray>', false, null, null, true, 0, true, false)");
                    stat1.setString(1, player.getUniqueId().toString());
                    stat1.setString(2, player.getName());
                    stat1.execute();
                    conn.prepareStatement("INSERT INTO stats(uuid, kills, deaths) VALUES('" + player.getUniqueId() + "', 0, 0)").execute();
                    conn.prepareStatement("INSERT INTO particleEffects(uuid, killEffect, particleEffect) VALUES('" + player.getUniqueId() + "', 'none', 'none')").execute();
                    playerDataMap.put(player.getUniqueId(), new PlayerData(
                            conn,
                            player.getUniqueId(),
                            player.getName(),
                            null,
                            1,
                            0,
                            true,
                            "<gray>",
                            false,
                            null,
                            null,
                            true,
                            0,
                            true,
                            false,
                            0
                    ));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static Map<Integer, PlayerData> balanceTop = new HashMap<>();

    public class BalanceTopTask extends BukkitRunnable {
        @Override
        public void run() {
            try {
                PreparedStatement stat = conn.prepareStatement("SELECT * FROM players ORDER BY balance DESC LIMIT 10");
                ResultSet set = stat.executeQuery();
                AtomicInteger i = new AtomicInteger(1);
                while(set.next()) balanceTop.put(
                        i.getAndIncrement(),
                        getPlayerData(UUID.fromString(set.getString("uuid")))
                );
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }
    }

    public String getParticleEffect(UUID uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM particleEffects WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) return set.getString("particleEffect");
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

    public String getKillEffect(UUID uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM particleEffects WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) return set.getString("killEffect");
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

    public void setParticleEffect(UUID uuid, @Nullable String particleEffect) {
        if(particleEffect == null) particleEffect = "none";
        try {
            conn.prepareStatement("UPDATE particleEffects SET particleEffect='" + particleEffect + "' WHERE uuid='" + uuid + "'").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void setKillEffect(UUID uuid, @Nullable String killEffect) {
        if(killEffect == null) killEffect = "none";
        try {
            conn.prepareStatement("UPDATE particleEffects SET killEffect='" + killEffect + "' WHERE uuid='" + uuid + "'").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public static final Map<Integer, String> killsLeaderboard = new HashMap<>();
    public static final Map<Integer, Integer> killsLeaderboardK = new HashMap<>();
    public static final Map<Integer, String> deathsLeaderboard = new HashMap<>();
    public static final Map<Integer, Integer> deathsLeaderboardD = new HashMap<>();
    public static final Map<Integer, String> playtimeLeaderboard = new HashMap<>();
    public static final Map<Integer, Long> playtimeLeaderboardP = new HashMap<>();
    public static final Map<Integer, String> killStreakLeaderboard = new HashMap<>();
    public static final Map<Integer, Integer> killStreakLeaderboardK = new HashMap<>();

    public void reloadLeaderboards() {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                ResultSet killsSet = conn.prepareStatement("SELECT * FROM stats ORDER BY kills DESC").executeQuery();
                for(int i = 0; i < 10; i++) {
                    if(killsSet.next()) {
                        PlayerData data = getPlayerData(UUID.fromString(killsSet.getString("uuid")));
                        killsLeaderboard.put(i+1, data.name);
                        killsLeaderboardK.put(i+1, killsSet.getInt("kills"));
                    }
                }
                ResultSet deathsSet = conn.prepareStatement("SELECT * FROM stats ORDER BY deaths DESC").executeQuery();
                for(int i = 0; i < 10; i++) {
                    if(deathsSet.next()) {
                        PlayerData data = getPlayerData(UUID.fromString(deathsSet.getString("uuid")));
                        deathsLeaderboard.put(i+1, data.name);
                        deathsLeaderboardD.put(i+1, deathsSet.getInt("deaths"));
                    }
                }
                ResultSet killStreakSet = conn.prepareStatement("SELECT * FROM players ORDER BY killStreak DESC").executeQuery();
                for(int i = 0; i < 10; i++) {
                    if(killStreakSet.next()) {
                        PlayerData data = getPlayerData(UUID.fromString(killStreakSet.getString("uuid")));
                        killStreakLeaderboard.put(i+1, data.name);
                        killStreakLeaderboardK.put(i+1, killStreakSet.getInt("killStreak"));
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    private final Map<String, PlayerStats> stats = new HashMap<>();

    public void addKill(String uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                conn.prepareStatement("UPDATE stats SET kills=kills+1 WHERE uuid='" + uuid +  "'").execute();
                if(stats.containsKey(uuid)) stats.get(uuid).addKill();
                else getStats(uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setKills(UUID uuid, int kills) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                conn.prepareStatement("UPDATE stats SET kills=" + kills + " WHERE uuid='" + uuid +  "'").execute();
                if(stats.containsKey(uuid.toString())) stats.get(uuid.toString()).kills = kills;
                else getStats(uuid.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addDeath(String uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                conn.prepareStatement("UPDATE stats SET deaths=deaths+1 WHERE uuid='" + uuid +  "'").execute();
                if(stats.containsKey(uuid)) stats.get(uuid).addDeath();
                else getStats(uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setDeaths(UUID uuid, int deaths) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                conn.prepareStatement("UPDATE stats SET deaths=" + deaths + " WHERE uuid='" + uuid +  "'").execute();
                if(stats.containsKey(uuid.toString())) stats.get(uuid.toString()).deaths = deaths;
                else getStats(uuid.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Nullable
    public PlayerStats getStats(String uuid) {
        if(stats.containsKey(uuid)) return stats.get(uuid);
        else {
            try {
                ResultSet set = conn.prepareStatement("SELECT * FROM stats WHERE uuid='" + uuid + "'").executeQuery();
                if(set.next()) {
                    PlayerStats stat = new PlayerStats(set.getInt("kills"), set.getInt("deaths"));
                    stats.put(uuid, stat);
                    return stat;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static class PlayerStats {
        public int kills;
        public int deaths;
        public String kdr;
        public PlayerStats(int kills1, int deaths1) {
            kills = kills1;
            deaths = deaths1;
            if(deaths == 0) {
                kdr = "Infinity";
            } else {
                kdr = (kills/deaths) + "";
            }
        }

        public void addKill() {
            this.kills = this.kills+1;
            if(deaths == 0) {
                kdr = "Infinity";
            } else {
                kdr = (kills/deaths) + "";
            }
        }
        public void addDeath() {
            this.deaths = this.deaths+1;
            kdr = (kills/deaths) + "";
        }
    }

}
