package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

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

            // Database Alters - To be removed in the future
            ResultSet set = playerConn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='players' AND COLUMN_NAME='night'").executeQuery();
            if(!set.next()) playerConn.prepareStatement("ALTER TABLE players" +
                    " ADD night BOOLEAN NOT NULL" +
                    " DEFAULT (true)").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
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
