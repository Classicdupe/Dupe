package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.io.File;
import java.sql.*;

public class Database {

    static Connection playerConn;
    static Connection serverConn;
    private FilterDatabase filterDatabase;
    private PlayerDatabase playerDatabase;
    public Location spawn;

    public Database() {
        try {
            Class.forName("org.h2.Driver");
            playerConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData");
            serverConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData");
            playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid varchar, name varchar, nickname varchar, timesjoined long, playtime long, randomitem BOOLEAN, chatcolor VARCHAR, gradient BOOLEAN, gradientfrom VARCHAR, gradientto VARCHAR)").execute();
            serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS filter(text varchar, fullword BOOLEAN)").execute();
            serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS spawn(spawn varchar, x DOUBLE, y DOUBLE, z DOUBLE, pitch FLOAT, yaw FLOAT, world varchar)").execute();
            playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid VARCHAR, kills INT, deaths INT)").execute();
            filterDatabase = new FilterDatabase(serverConn);
            playerDatabase = new PlayerDatabase(playerConn);
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        spawn = getSpawn();
    }

    public void setSpawn(Location loc) {
        try {
            spawn = loc;
            serverConn.prepareStatement("DELETE FROM spawn WHERE spawn='spawn'").execute();
            PreparedStatement stat = serverConn.prepareStatement("INSERT INTO spawn(spawn, x, y, z, pitch, yaw, world) VALUES('spawn', ?, ?, ?, ?, ?, ?)");
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
            ResultSet set = serverConn.prepareStatement("SELECT * FROM spawn WHERE spawn='spawn'").executeQuery();
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

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }

    public void shutdown() {
        try {
            playerConn.close();
            serverConn.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

}
