package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<Integer, PlayerData> killsLeaderboard = new HashMap<>();
    public static Map<Integer, PlayerData> deathsLeaderboard = new HashMap<>();

    public void reloadLeaderboards() {
        try {
            ResultSet killsSet = conn.prepareStatement("SELECT * FROM players ORDER BY kills ASC").executeQuery();
            for(int i = 0; i < 10; i++) {
                if(killsSet.next()) {
                    killsLeaderboard.put(i+1, new PlayerData(
                            killsSet.getString("uuid"),
                            killsSet.getString("name"),
                            killsSet.getString("nickname"),
                            killsSet.getLong("timesjoined"),
                            killsSet.getLong("playtime"),
                            killsSet.getBoolean("randomitem"),
                            killsSet.getString("chatcolor"),
                            killsSet.getBoolean("gradient"),
                            killsSet.getString("gradientfrom"),
                            killsSet.getString("gradientto")
                    ));
                }
            }
            ResultSet deathsSet = conn.prepareStatement("SELECT * FROM players ORDER BY deaths ASC").executeQuery();
            for(int i = 0; i < 10; i++) {
                if(deathsSet.next()) {
                    deathsLeaderboard .put(i+1, new PlayerData(
                            deathsSet.getString("uuid"),
                            deathsSet.getString("name"),
                            deathsSet.getString("nickname"),
                            deathsSet.getLong("timesjoined"),
                            deathsSet.getLong("playtime"),
                            deathsSet.getBoolean("randomitem"),
                            deathsSet.getString("chatcolor"),
                            deathsSet.getBoolean("gradient"),
                            deathsSet.getString("gradientfrom"),
                            deathsSet.getString("gradientto")
                    ));
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
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
            conn.prepareStatement("UPDATE players SET nickname='" + nickname + "' WHERE uuid='" + uuid +  "'").execute();
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

    public void addKill(String uuid) {
        try {
            conn.prepareStatement("UPDATE stats SET kills=kills+1 WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDeath(String uuid) {
        try {
            conn.prepareStatement("UPDATE stats SET deaths=deaths+1 WHERE uuid='" + uuid +  "'").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public PlayerStats getStats(String uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM stats WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) return new PlayerStats(set.getInt("kills"), set.getInt("deaths"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static class PlayerStats {
        public static int kills;
        public static int deaths;
        public static String kdr;
        public PlayerStats(int kills1, int deaths1) {
            kills = kills1;
            deaths = deaths1;
            if(deaths == 0) {
                kdr = "Infinity";
            } else {
                kdr = (kills/deaths) + "";
            }
        }
    }

    public void initPlayer(Player player) {
        try {
            PreparedStatement stat = conn.prepareStatement("INSERT INTO players(uuid, name, nickname, timesjoined, playtime, randomitem, chatcolor, gradient, gradientfrom, gradientto) VALUES (?, ?, null, 1, 0, true, '&7', false, null, null)");
            stat.setString(1, player.getUniqueId().toString());
            stat.setString(2, player.getName());
            stat.execute();
            conn.prepareStatement("INSERT INTO stats(uuid, kills, deaths) VALUES('" + player.getUniqueId() + "', 0, 0)").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
