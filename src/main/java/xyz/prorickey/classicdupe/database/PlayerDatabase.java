package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDatabase {

    Connection conn;

    public PlayerDatabase(Connection conn) {
        this.conn = conn;
    }

    public static class PlayerData {

        public String uuid;
        public String name;
        public String nickname;
        public long timesjoined;
        public long playtime;
        public boolean randomitem;
        public String chatcolor;
        public boolean gradient;
        public String gradientfrom;
        public String gradientto;

        public PlayerData(String uuid1,
                          String name1,
                          String nickname1,
                          long timesjoined1,
                          long playtime1,
                          boolean randomitem1,
                          String chatcolor1,
                          boolean gradient1,
                          String gradientfrom1,
                          String gradientto1
        ) {
            uuid = uuid1;
            name = name1;
            nickname = nickname1;
            timesjoined = timesjoined1;
            playtime = playtime1;
            randomitem = randomitem1;
            chatcolor = chatcolor1;
            gradient = gradient1;
            gradientfrom = gradientfrom1;
            gradientto = gradientto1;
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

    public static Map<Integer, String> killsLeaderboard = new HashMap<>();
    public static Map<Integer, Integer> killsLeaderboardK = new HashMap<>();
    public static Map<Integer, String> deathsLeaderboard = new HashMap<>();
    public static Map<Integer, Integer> deathsLeaderboardD = new HashMap<>();

    public void reloadLeaderboards() {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                ResultSet killsSet = conn.prepareStatement("SELECT * FROM stats ORDER BY kills DESC").executeQuery();
                for(int i = 0; i < 10; i++) {
                    if(killsSet.next()) {
                        PlayerData data = getPlayer(killsSet.getString("uuid"));
                        killsLeaderboard.put(i+1, data.name);
                        killsLeaderboardK.put(i+1, killsSet.getInt("kills"));
                    }
                }
                ResultSet deathsSet = conn.prepareStatement("SELECT * FROM stats ORDER BY deaths DESC").executeQuery();
                for(int i = 0; i < 10; i++) {
                    if(deathsSet.next()) {
                        PlayerData data = getPlayer(deathsSet.getString("uuid"));
                        deathsLeaderboard.put(i+1, data.name);
                        deathsLeaderboardD.put(i+1, deathsSet.getInt("deaths"));
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public PlayerData getPlayer(String uuid) {
        ResultSet set = null;
        try {
            set = conn.prepareStatement("SELECT * FROM players WHERE uuid='" + uuid + "'").executeQuery();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        try {
            assert set != null;
            if(!set.next()) return null;
            return new PlayerData(
                    set.getString("uuid"),
                    set.getString("name"),
                    set.getString("nickname"),
                    set.getLong("timesjoined"),
                    set.getLong("playtime"),
                    set.getBoolean("randomitem"),
                    set.getString("chatcolor"),
                    set.getBoolean("gradient"),
                    set.getString("gradientfrom"),
                    set.getString("gradientto")
            );
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

    public void setNickname(String uuid, String nickname) {
        try {
            PreparedStatement stat = conn.prepareStatement("UPDATE players SET nickname=? WHERE uuid='" + uuid +  "'");
            stat.setString(1, nickname);
            stat.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void resetNickname(String uuid) {
        try {
            conn.prepareStatement("UPDATE players SET nickname=null WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public Boolean swapRandomItem(String uuid) {
        PlayerData data = getPlayer(uuid);
        if(data.randomitem) {
            disableRandomItem(uuid);
            return false;
        } else {
            enableRandomItem(uuid);
            return true;
        }
    }

    public void setChatColor(String uuid, String color) {
        try {
            conn.prepareStatement("UPDATE players SET chatcolor='" + color + "' WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public Boolean toggleGradient(String uuid) {
        try {
            if(getPlayer(uuid).gradient) {
                conn.prepareStatement("UPDATE players SET gradient=false WHERE uuid='" + uuid +  "'").execute();
                return false;
            } else {
                conn.prepareStatement("UPDATE players SET gradient=true WHERE uuid='" + uuid +  "'").execute();
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatGradientCMD.GradientProfiles getGradientProfile(String uuid) {
        PlayerData data = getPlayer(uuid);
        return new ChatGradientCMD.GradientProfiles(
                data.gradientfrom,
                data.gradientto
        );
    }

    public void setGradientProfile(String uuid, ChatGradientCMD.GradientProfiles profile) {
        try {
            conn.prepareStatement("UPDATE players SET gradientFrom='" + profile.gradientFrom + "' WHERE uuid='" + uuid +  "'").execute();
            conn.prepareStatement("UPDATE players SET gradientTo='" + profile.gradientTo + "' WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void enableRandomItem(String uuid) {
        try {
            conn.prepareStatement("UPDATE players SET randomitem=true WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disableRandomItem(String uuid) {
        try {
            conn.prepareStatement("UPDATE players SET randomitem=false WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, PlayerStats> stats = new HashMap<>();

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
            this.kills = this.kills+1;
            if(deaths == 0) {
                kdr = "Infinity";
            } else {
                kdr = (kills/deaths) + "";
            }
        }
    }

    public void initPlayer(Player player) {
        try {
            PreparedStatement stat = conn.prepareStatement("INSERT INTO players(uuid, name, nickname, timesjoined, playtime, randomitem, chatcolor, gradient, gradientfrom, gradientto) VALUES (?, ?, null, 1, 0, true, '<gray>', false, null, null)");
            stat.setString(1, player.getUniqueId().toString());
            stat.setString(2, player.getName());
            stat.execute();
            conn.prepareStatement("INSERT INTO stats(uuid, kills, deaths) VALUES('" + player.getUniqueId() + "', 0, 0)").execute();
            conn.prepareStatement("INSERT INTO particleEffects(uuid, killEffect, particleEffect) VALUES('" + player.getUniqueId() + "', 'none', 'none')").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
