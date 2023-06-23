package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.clans.databases.H2ClanDatabase;
import xyz.prorickey.classicdupe.clans.databases.MariaClanDatabase;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    static Connection conn;
    static Connection playerConn;
    static Connection serverConn;
    static Connection linkingConn;
    static Connection homesConn;
    static Connection bountyConn;
    private FilterDatabase filterDatabase;
    private PlayerDatabase playerDatabase;
    private LinkingDatabase linkingDatabase;
    private HomesDatabase homesDatabase;
    private BountyDatabase bountyDatabase;
    public static List<Player> blockedToUseCommands = new ArrayList<>();

    public Database() {
        try {
            if(Config.getConfig().getBoolean("database.mariadb")) {
                conn = DriverManager.getConnection(
                        "jdbc:mariadb://" + Config.getConfig().getString("database.host") + ":3306/classicdupe",
                        Config.getConfig().getString("database.user"),
                        Config.getConfig().getString("database.password")
                );

                Bukkit.getLogger().info("Connected to MariaDB database!");

                ClassicDupe.clanDatabase = new MariaClanDatabase(ClassicDupe.getPlugin(), conn);

                conn.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid TEXT, name TEXT, nickname TEXT, " +
                        "timesjoined long, playtime long, randomitem BOOLEAN, " +
                        "chatcolor TEXT, gradient BOOLEAN, gradientfrom TEXT, " +
                        "gradientto TEXT, night BOOLEAN, balance BIGINT, " +
                        "deathmessages BOOLEAN, mutepings BOOLEAN, killStreak INT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS filter(text TEXT, fullword BOOLEAN)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS spawn(spawn TEXT, x DOUBLE, y DOUBLE, z DOUBLE, pitch FLOAT, yaw FLOAT, world TEXT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid TEXT, kills INT, deaths INT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS particleEffects(uuid TEXT, killEffect TEXT, particleEffect TEXT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS link(uuid TEXT, dscid Long)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS homes(uuid TEXT, name TEXT, world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS bounties(uuid TEXT, amount BIGINT)").execute();

                Bukkit.getLogger().info("Created tables!");

                if(new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData.mv.db").exists()) {

                    playerConn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData");

                    System.out.println("Connected to H2 database!");

                    try(ResultSet set = conn.prepareStatement("SELECT * FROM players").executeQuery()) {
                        if(!set.next()) {
                            PreparedStatement playerStat = playerConn.prepareStatement("SELECT * FROM players");
                            ResultSet playerSet = playerStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO" +
                                    " players(uuid, name, nickname, timesjoined, playtime, randomitem, chatcolor, gradient, gradientfrom, gradientto, night, balance, deathmessages, mutepings, killStreak)" +
                                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
                                stat.setLong(12, playerSet.getInt("balance"));
                                stat.setBoolean(13, playerSet.getBoolean("deathmessages"));
                                stat.setBoolean(14, playerSet.getBoolean("mutepings"));
                                stat.setInt(15, playerSet.getInt("killStreak"));
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
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData.mv.db").delete();

                }

                playerConn = conn;

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
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData.mv.db").delete();
                }

                serverConn = conn;

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
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "linkData.mv.db").delete();
                }

                linkingConn = conn;

                if(new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "homes.mv.db").exists()) {

                    homesConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "homes");

                    try (ResultSet set = conn.prepareStatement("SELECT * FROM homes").executeQuery()) {
                        if (!set.next()) {
                            PreparedStatement homeStat = linkingConn.prepareStatement("SELECT * FROM homes");
                            ResultSet homeSet = homeStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO homes(uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                            while (homeSet.next()) {
                                stat.setString(1, homeSet.getString("uuid"));
                                stat.setString(2, homeSet.getString("name"));
                                stat.setString(3, homeSet.getString("world"));
                                stat.setDouble(4, homeSet.getDouble("x"));
                                stat.setDouble(5, homeSet.getDouble("y"));
                                stat.setDouble(6, homeSet.getDouble("z"));
                                stat.setFloat(7, homeSet.getFloat("yaw"));
                                stat.setFloat(8, homeSet.getFloat("pitch"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    homesConn.close();
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "homes.mv.db").delete();
                }

                homesConn = conn;

                if(new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "bountyData.mv.db").exists()) {
                    bountyConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "bountyData");

                    try (ResultSet set = conn.prepareStatement("SELECT * FROM bounty").executeQuery()) {
                        if (!set.next()) {
                            PreparedStatement bountyStat = bountyConn.prepareStatement("SELECT * FROM bounty");
                            ResultSet bountySet = bountyStat.executeQuery();
                            PreparedStatement stat = conn.prepareStatement("INSERT INTO bounty(uuid, amount) VALUES (?, ?)");
                            while (bountySet.next()) {
                                stat.setString(1, bountySet.getString("uuid"));
                                stat.setDouble(2, bountySet.getDouble("amount"));
                                stat.addBatch();
                            }
                            stat.executeLargeBatch();
                        }
                    }

                    bountyConn.close();
                    new File(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "bountyData.mv.db").delete();
                }

                bountyConn = conn;

                filterDatabase = new FilterDatabase(conn);
                playerDatabase = new PlayerDatabase(conn);
                linkingDatabase = new LinkingDatabase(conn);
                homesDatabase = new HomesDatabase(conn);
                bountyDatabase = new BountyDatabase(conn);

            } else {
                ClassicDupe.clanDatabase = new H2ClanDatabase(ClassicDupe.getPlugin());

                playerConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "playerData");
                serverConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "serverData");
                linkingConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "linkData");
                homesConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "homes");
                bountyConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "bountyData");

                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid varchar, name varchar, nickname varchar, " +
                        "timesjoined long, playtime long, randomitem BOOLEAN, " +
                        "chatcolor VARCHAR, gradient BOOLEAN, gradientfrom VARCHAR, " +
                        "gradientto VARCHAR, night BOOLEAN, balance BIGINT, " +
                        "deathmessages BOOLEAN, mutepings BOOLEAN, killStreak INT)").execute();
                serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS filter(text varchar, fullword BOOLEAN)").execute();
                serverConn.prepareStatement("CREATE TABLE IF NOT EXISTS spawn(spawn varchar, x DOUBLE, y DOUBLE, z DOUBLE, pitch FLOAT, yaw FLOAT, world varchar)").execute();
                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid VARCHAR, kills INT, deaths INT)").execute();
                playerConn.prepareStatement("CREATE TABLE IF NOT EXISTS particleEffects(uuid VARCHAR, killEffect VARCHAR, particleEffect VARCHAR)").execute();
                linkingConn.prepareStatement("CREATE TABLE IF NOT EXISTS link(uuid VARCHAR, dscid Long)").execute();
                homesConn.prepareStatement("CREATE TABLE IF NOT EXISTS homes(uuid VARCHAR, name VARCHAR, world VARCHAR, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)").execute();
                bountyConn.prepareStatement("CREATE TABLE IF NOT EXISTS bounties(uuid VARCHAR, amount BIGINT)").execute();

                filterDatabase = new FilterDatabase(serverConn);
                playerDatabase = new PlayerDatabase(playerConn);
                linkingDatabase = new LinkingDatabase(linkingConn);
                homesDatabase = new HomesDatabase(homesConn);
                bountyDatabase = new BountyDatabase(bountyConn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loadSpawns();
    }

    public Map<String, Location> spawns = new HashMap<>();

    public void setSpawn(String name, Location loc) {
        spawns.put(name, loc);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = serverConn.prepareStatement("INSERT INTO spawn(spawn, x, y, z, pitch, yaw, world) VALUES(?, ?, ?, ?, ?, ?, ?)");
                stat.setString(1, name);
                stat.setDouble(2, loc.getX());
                stat.setDouble(3, loc.getY());
                stat.setDouble(4, loc.getZ());
                stat.setFloat(5, loc.getPitch());
                stat.setFloat(6, loc.getYaw());
                stat.setString(7, loc.getWorld().getName());
                stat.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadSpawns() {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                ResultSet set = serverConn.prepareStatement("SELECT * FROM spawn").executeQuery();
                while(set.next()) {
                    spawns.put(
                            set.getString("spawn"),
                            new Location(
                                    Bukkit.getWorld(set.getString("world")),
                                    set.getDouble("x"),
                                    set.getDouble("y"),
                                    set.getDouble("z"),
                                    set.getFloat("yaw"),
                                    set.getFloat("pitch")
                            )
                    );
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Location getSpawn(String name) {
        return spawns.get(name);
    }

    public FilterDatabase getFilterDatabase() { return filterDatabase; }
    public PlayerDatabase getPlayerDatabase() { return playerDatabase; }
    public LinkingDatabase getLinkingDatabase() { return linkingDatabase; }
    public HomesDatabase getHomesDatabase() { return homesDatabase; }
    public BountyDatabase getBountyDatabase() { return bountyDatabase; }

}
