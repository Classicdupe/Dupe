package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mariadb.jdbc.export.Prepare;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    static Connection conn;
    static Connection playerConn;
    static Connection serverConn;
    static Connection linkingConn;
    private FilterDatabase filterDatabase;
    private PlayerDatabase playerDatabase;
    private LinkingDatabase linkingDatabase;
    public Location spawn;
    public static List<Player> blockedToUseCommands = new ArrayList<>();

    public Database() {
        try {

            // Database Alters - To be removed in the future
            ResultSet set = playerConn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='players' AND COLUMN_NAME='night'").executeQuery();
            try {
                if (!set.next()) playerConn.prepareStatement("ALTER TABLE players" +
                        " ADD night BOOLEAN NOT NULL" +
                        " DEFAULT (true)").execute();
            } catch (SQLException ee) {
                Bukkit.getLogger().warning("CODE ERR: tried to recreate column 'night' in table 'players' but failed because its already there");
            if(Config.getConfig().getBoolean("database.mariadb")) {
                conn = DriverManager.getConnection(
                        "jdbc:mariadb://localhost:3306/classicdupe",
                        Config.getConfig().getString("database.user"),
                        Config.getConfig().getString("database.password")
                );

                System.out.println("Connected to MariaDB database!");

                conn.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid TEXT, name TEXT, nickname TEXT, timesjoined long, playtime long, randomitem BOOLEAN, chatcolor TEXT, gradient BOOLEAN, gradientfrom TEXT, gradientto TEXT, night BOOLEAN)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS filter(text TEXT, fullword BOOLEAN)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS spawn(spawn TEXT, x DOUBLE, y DOUBLE, z DOUBLE, pitch FLOAT, yaw FLOAT, world TEXT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid TEXT, kills INT, deaths INT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS particleEffects(uuid TEXT, killEffect TEXT, particleEffect TEXT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS link(uuid TEXT, dscid Long)").execute();

                System.out.println("Created tables!");

                if(new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData.mv.db").exists()) {

                    playerConn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData");

                    System.out.println("Connected to H2 database!");

                    try(ResultSet set = conn.prepareStatement("SELECT * FROM players").executeQuery()) {
                        if(!set.next()) {
                            PreparedStatement playerStat = playerConn.prepareStatement("SELECT * FROM players");
                            ResultSet playerSet = playerStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO" +
                                    " players(uuid, name, nickname, timesjoined, playtime, randomitem, chatcolor, gradient, gradientfrom, gradientto, night)" +
                                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            while(playerSet.next()) {
                                stat.setString(1, playerSet.getString("uuid"));
                                stat.setString(2, playerSet.getString("name"));
                                stat.setString(3, playerSet.getString("nickname"));
                                stat.setLong(4, playerSet.getLong("timesjoined"));
                                stat.setLong(5, playerSet.getLong("playtime"));
                                stat.setBoolean(6, playerSet.getBoolean("randomitem"));
                                stat.setString(7, playerSet.getString("chatcolor"));
                                stat.setBoolean(8, playerSet.getBoolean("gradient"));
                                stat.setString(9, playerSet.getString("gradientfrom"));
                                stat.setString(10, playerSet.getString("gradientto"));
                                stat.setBoolean(11, playerSet.getBoolean("night"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    try(ResultSet set = conn.prepareStatement("SELECT * FROM stats").executeQuery()) {
                        if(!set.next()) {
                            PreparedStatement playerStat = playerConn.prepareStatement("SELECT * FROM stats");
                            ResultSet playerSet = playerStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO stats(uuid, kills, deaths) VALUES (?, ?, ?)");
                            while(playerSet.next()) {
                                stat.setString(1, playerSet.getString("uuid"));
                                stat.setInt(2, playerSet.getInt("kills"));
                                stat.setInt(3, playerSet.getInt("deaths"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    try(ResultSet set = conn.prepareStatement("SELECT * FROM particleEffects").executeQuery()) {
                        if(!set.next()) {
                            PreparedStatement playerStat = playerConn.prepareStatement("SELECT * FROM particleEffects");
                            ResultSet playerSet = playerStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO particleEffects(uuid, killEffect, particleEffect) VALUES (?, ?, ?)");
                            while(playerSet.next()) {
                                stat.setString(1, playerSet.getString("uuid"));
                                stat.setString(2, playerSet.getString("killEffect"));
                                stat.setString(3, playerSet.getString("particleEffect"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    playerConn.close();
                    playerConn = conn;
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData.mv.db").delete();

                }

                if(new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData.mv.db").exists()) {

                    serverConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData");

                    try(ResultSet set = conn.prepareStatement("SELECT * FROM filter").executeQuery()) {
                        if(!set.next()) {
                            PreparedStatement serverStat = serverConn.prepareStatement("SELECT * FROM filter");
                            ResultSet serverSet = serverStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO filter(text, fullword) VALUES (?, ?)");
                            while(serverSet.next()) {
                                stat.setString(1, serverSet.getString("text"));
                                stat.setBoolean(2, serverSet.getBoolean("fullword"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    try(ResultSet set = conn.prepareStatement("SELECT * FROM spawn").executeQuery()) {
                        if(!set.next()) {
                            PreparedStatement serverStat = serverConn.prepareStatement("SELECT * FROM spawn");
                            ResultSet serverSet = serverStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO spawn(spawn, x, y, z, pitch, yaw, world) VALUES (?, ?, ?, ?, ?, ?, ?)");
                            while(serverSet.next()) {
                                stat.setString(1, serverSet.getString("spawn"));
                                stat.setDouble(2, serverSet.getDouble("x"));
                                stat.setDouble(3, serverSet.getDouble("y"));
                                stat.setDouble(4, serverSet.getDouble("z"));
                                stat.setFloat(5, serverSet.getFloat("pitch"));
                                stat.setFloat(6, serverSet.getFloat("yaw"));
                                stat.setString(7, serverSet.getString("world"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    serverConn.close();
                    serverConn = conn;
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData.mv.db").delete();
                }

                if(new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "linkData.mv.db").exists()) {

                    linkingConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "linkData");

                    try (ResultSet set = conn.prepareStatement("SELECT * FROM link").executeQuery()) {
                        if (!set.next()) {
                            PreparedStatement linkStat = linkingConn.prepareStatement("SELECT * FROM link");
                            ResultSet linkSet = linkStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO link(uuid, dscid) VALUES (?, ?)");
                            while (linkSet.next()) {
                                stat.setString(1, linkSet.getString("uuid"));
                                stat.setLong(2, linkSet.getLong("dscid"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    linkingConn.close();
                    linkingConn = conn;
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "linkData.mv.db").delete();
                }

                filterDatabase = new FilterDatabase(serverConn);
                playerDatabase = new PlayerDatabase(playerConn);
                linkingDatabase = new LinkingDatabase(linkingConn);

            } else {
                playerConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData");
                serverConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData");
                linkingConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "linkData");

                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid varchar, name varchar, nickname varchar, timesjoined long, playtime long, randomitem BOOLEAN, chatcolor VARCHAR, gradient BOOLEAN, gradientfrom VARCHAR, gradientto VARCHAR, night BOOLEAN)").execute();
                serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS filter(text varchar, fullword BOOLEAN)").execute();
                serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS spawn(spawn varchar, x DOUBLE, y DOUBLE, z DOUBLE, pitch FLOAT, yaw FLOAT, world varchar)").execute();
                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid VARCHAR, kills INT, deaths INT)").execute();
                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS particleEffects(uuid VARCHAR, killEffect VARCHAR, particleEffect VARCHAR)").execute();
                linkingConn.prepareStatement("CREATE TABLE IF NOT EXISTS link(uuid VARCHAR, dscid Long)").execute();

                filterDatabase = new FilterDatabase(serverConn);
                playerDatabase = new PlayerDatabase(playerConn);
                linkingDatabase = new LinkingDatabase(linkingConn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        spawn = getSpawn();
    }

    public void setNetherSpawn(Location loc) {
        try {
            spawn = loc;
            serverConn.prepareStatement("DELETE FROM spawn WHERE spawn='nether'").execute();
            PreparedStatement stat = serverConn.prepareStatement("INSERT INTO spawn(spawn, x, y, z, pitch, yaw, world) VALUES('nether', ?, ?, ?, ?, ?, ?)");
            stat.setDouble(1, loc.getX());
            stat.setDouble(2, loc.getY());
            stat.setDouble(3, loc.getZ());
            stat.setFloat(4, loc.getPitch());
            stat.setFloat(5, loc.getYaw());
            stat.setString(6, loc.getWorld().getName());
            stat.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getNetherSpawn() {
        try {
            ResultSet set = serverConn.prepareStatement("SELECT * FROM spawn WHERE spawn='nether'").executeQuery();
            if(set.next()) {
                return new Location(
                        ClassicDupe.getPlugin().getServer().getWorld(set.getString("world")),
                        set.getDouble("x"),
                        set.getDouble("y"),
                        set.getDouble("z"),
                        set.getFloat("yaw"),
                        set.getFloat("pitch")
                );
            } else {
                return ClassicDupe.getPlugin().getServer().getWorld("world_nether").getSpawnLocation();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSpawn(Location loc) {
        try {
            spawn = loc;
            serverConn.prepareStatement("DELETE FROM spawn WHERE spawn='overworld'").execute();
            PreparedStatement stat = serverConn.prepareStatement("INSERT INTO spawn(spawn, x, y, z, pitch, yaw, world) VALUES('overworld', ?, ?, ?, ?, ?, ?)");
            stat.setDouble(1, loc.getX());
            stat.setDouble(2, loc.getY());
            stat.setDouble(3, loc.getZ());
            stat.setFloat(4, loc.getPitch());
            stat.setFloat(5, loc.getYaw());
            stat.setString(6, loc.getWorld().getName());
            stat.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getSpawn() {
        try {
            ResultSet set = serverConn.prepareStatement("SELECT * FROM spawn WHERE spawn='overworld'").executeQuery();
            if(set.next()) {
                return new Location(
                        ClassicDupe.getPlugin().getServer().getWorld(set.getString("world")),
                        set.getDouble("x"),
                        set.getDouble("y"),
                        set.getDouble("z"),
                        set.getFloat("yaw"),
                        set.getFloat("pitch")
                );
            } else {
                return ClassicDupe.getPlugin().getServer().getWorld("world").getSpawnLocation();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FilterDatabase getFilterDatabase() {
        return filterDatabase;
    }

    public PlayerDatabase getPlayerDatabase() { return playerDatabase; }
    public LinkingDatabase getLinkingDatabase() { return linkingDatabase; }

    public void shutdown() {
        try {
            playerConn.close();
            serverConn.close();
            linkingConn.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

}
