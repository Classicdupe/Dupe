package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDatabase {

    Connection conn;

    public PlayerDatabase(Connection conn) {
        this.conn = conn;
    }

    public class PlayerData {

        public String uuid;
        public String name;
        public String nickname;
        public long timesjoined;
        public long playtime;
        public boolean randomitem;

        public PlayerData(String uuid1, String name1, String nickname1, long timesjoined1, long playtime1, boolean randomitem1) {
            uuid = uuid1;
            name = name1;
            nickname = nickname1;
            timesjoined = timesjoined1;
            playtime = playtime1;
            randomitem = randomitem1;
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
            if(!set.next()) return null;
            return new PlayerData(
                    set.getString("uuid"),
                    set.getString("name"),
                    set.getString("nickname"),
                    set.getLong("timesjoined"),
                    set.getLong("playtime"),
                    set.getBoolean("randomitem")
            );
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
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

    public void initPlayer(Player player) {
        try {
            PreparedStatement stat = conn.prepareStatement("INSERT INTO players(uuid, name, nickname, timesjoined, playtime, randomitem) VALUES (?, ?, null, 1, 0, true)");
            stat.setString(1, player.getUniqueId().toString());
            stat.setString(2, player.getName());
            stat.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
