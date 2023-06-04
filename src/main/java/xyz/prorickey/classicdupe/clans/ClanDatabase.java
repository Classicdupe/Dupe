package xyz.prorickey.classicdupe.clans;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.util.IOUtils;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ClanDatabase {

    public static File dataDir;
    public static File globalConfigFile;
    public static YamlConfiguration globalConfig;
    private static Connection main;

    private static final List<String> clanNames = new ArrayList<>();
    private static final Map<UUID, Clan> clansById = new HashMap<>();
    private static final Map<String, Clan> clansByName = new HashMap<>();
    private static final Map<UUID, ClanMember> clanMembers = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        dataDir = new File(plugin.getDataFolder() + "/clansData/");
        dataDir.mkdirs();
        globalConfigFile = new File(plugin.getDataFolder() + "/clansData/global.yml");
        if(!globalConfigFile.exists()) {
            try(InputStream inputStream = plugin.getResource("global.yml");
                OutputStream outputStream = new FileOutputStream(globalConfigFile)) {
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy global.yml resource", e);
            }
        }

        globalConfig = YamlConfiguration.loadConfiguration(globalConfigFile);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                main = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/main");
                // x y z pitch yaw world
                main.prepareStatement("CREATE TABLE IF NOT EXISTS clans(clanId VARCHAR, clanName VARCHAR, clanKills INT, publicClan BOOLEAN, clanColor VARCHAR)").execute();
                main.prepareStatement("CREATE TABLE IF NOT EXISTS clanWarps(clanId VARCHAR, name VARCHAR, levelNeeded INT, x INT, y INT, z INT, pitch FLOAT, yaw FLOAT, world VARCHAR)").execute();
                main.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid VARCHAR, name VARCHAR, clanId VARCHAR, clanName VARCHAR, level INT, boosts INT)").execute();

                try (ResultSet clanSet = main.prepareStatement("SELECT * FROM clans").executeQuery()) {
                    while (clanSet.next()) {
                        Clan clan = new Clan(UUID.fromString(clanSet.getString("clanId")), clanSet.getString("clanName"));
                        String clanColor = clanSet.getString("clanColor");
                        if (clanColor.startsWith("&")) {
                            String convertedColor = Utils.convertColorCodesToAdventure(clanColor);
                            clan.setClanColor(convertedColor);
                            PreparedStatement stat = main.prepareStatement("UPDATE clans SET clanColor=? WHERE clanId=?");
                            stat.setString(1, convertedColor);
                            stat.setString(2, clanSet.getString("clanId"));
                            stat.execute();
                        } else {
                            clan.setClanColor(clanColor);
                        }
                        clan.setPublicClan(clanSet.getBoolean("publicClan"));
                        clansById.put(clan.getClanId(), clan);
                        clansByName.put(clan.getClanName(), clan);
                        clanNames.add(clan.getClanName());
                    }
                }

                try (ResultSet warpSet = main.prepareStatement("SELECT * FROM clanWarps").executeQuery()) {
                    while(warpSet.next()) {
                        Clan clan = clansById.get(UUID.fromString(warpSet.getString("clanId")));
                        Location loc = new Location(Bukkit.getWorld(warpSet.getString("world")), warpSet.getInt("x"), warpSet.getInt("y"), warpSet.getInt("z"), warpSet.getFloat("yaw"), warpSet.getFloat("pitch"));
                        clan.setWarp(new Warp(warpSet.getString("name"), loc, warpSet.getInt("levelNeeded")));
                    }
                }

                try (ResultSet players = main.prepareStatement("SELECT p.*, c.clanId, c.clanName, c.clanColor, c.publicClan FROM players p LEFT JOIN clans c ON p.clanId = c.clanId").executeQuery()) {
                    while (players.next()) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(players.getString("uuid")));
                        ClanMember cmem = new ClanMember(offPlayer);
                        if (players.getString("clanId") != null) {
                            Clan clan = clansById.get(UUID.fromString(players.getString("clanId")));
                            if (players.getString("clanColor").startsWith("&")) {
                                clan.setClanColor(Utils.convertColorCodesToAdventure(players.getString("clanColor")));
                                PreparedStatement stat = main.prepareStatement("UPDATE clans SET clanColor=? WHERE clanId=?");
                                stat.setString(1, Utils.convertColorCodesToAdventure(players.getString("clanColor")));
                                stat.setString(2, players.getString("clanId"));
                                stat.execute();
                            } else {
                                clan.setClanColor(players.getString("clanColor"));
                            }
                            clan.setPublicClan(players.getBoolean("publicClan"));
                            switch (players.getInt("level")) {
                                case 0 -> clan.addDefault(offPlayer);
                                case 1 -> clan.addVip(offPlayer);
                                case 2 -> clan.addAdmin(offPlayer);
                                case 3 -> clan.setOwner(offPlayer);
                            }
                            clan.addPlayer(offPlayer);
                            cmem.setClan(clan, players.getInt("level"));
                        }
                        clanMembers.put(offPlayer.getUniqueId(), cmem);
                    }
                }

                // Fix for clans that have no players
                clansById.values()
                    .stream().filter(clan -> clan.getMembers().size() == 0).toList()
                    .forEach(clan -> {
                        if (clan.getMembers().size() == 0) {
                            clanNames.remove(clan.getClanName());
                            clansById.remove(clan.getClanId());
                            clansByName.remove(clan.getClanName());
                            try {
                                PreparedStatement stat = main.prepareStatement("DELETE FROM clans WHERE clanId=?");
                                stat.setString(1, clan.getClanId().toString());
                                stat.execute();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static YamlConfiguration getGlobalConfig() { return globalConfig; }

    public static void createClan(String name, Player owner) {
        UUID id = genClanId();
        Clan clan = new Clan(id, name);
        clan.setOwner(owner);
        clan.addPlayer(owner);
        ClanMember cmem = clanMembers.get(owner.getUniqueId());
        cmem.setClan(clan, 3);
        clansById.put(id, clan);
        clansByName.put(clan.getClanName(), clan);
        clanNames.add(clan.getClanName());
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement insertClanStmt = main.prepareStatement("INSERT INTO clans(clanId, clanName, clanKills, publicClan, clanColor) VALUES(?, ?, 0, false, '<yellow>')");
                insertClanStmt.setString(1, id.toString());
                insertClanStmt.setString(2, name);
                insertClanStmt.addBatch();

                PreparedStatement updatePlayerStmt = main.prepareStatement("UPDATE players SET clanId=?, clanName=?, level=3 WHERE uuid=?");
                updatePlayerStmt.setString(1, id.toString());
                updatePlayerStmt.setString(2, name);
                updatePlayerStmt.setString(3, owner.getUniqueId().toString());
                updatePlayerStmt.addBatch();

                insertClanStmt.executeBatch();
                updatePlayerStmt.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static UUID genClanId() {
        UUID uuid = UUID.randomUUID();
        if(getClan(uuid) != null) return genClanId();
        return uuid;
    }

    public static List<String> getLoadedClanNames() { return clanNames; }

    @Nullable
    public static Clan getClan(UUID id) { return clansById.get(id); }

    @Nullable
    public static Clan getClan(String name) {
        AtomicReference<Clan> foundClan = new AtomicReference<>(null);
        clansByName.forEach((s, clan) -> { if(s.equalsIgnoreCase(name)) foundClan.set(clan); });
        return foundClan.get();
    }

    public static ClanMember getClanMember(UUID uuid) { return clanMembers.get(uuid); }

    public static void createIfNotExists(Player player) {
        if(clanMembers.containsKey(player.getUniqueId())) return;
        clanMembers.put(player.getUniqueId(), new ClanMember(player));
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try (PreparedStatement stmt = main.prepareStatement(
                    "INSERT INTO players(uuid, name, clanId, clanName, level, boosts) " +
                            "VALUES (?, ?, null, null, null, 0)")) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, player.getName());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void removeClan(ClanMember cmem) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE uuid='" + cmem.getOffPlayer().getUniqueId() + "'").execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void deleteClan(Clan clan) {
        UUID clanId = clan.getClanId();
        clanMembers.values().stream().filter(c -> (c.getClanID() != null && c.getClanID().equals(clanId))).forEach(cmem -> {
            cmem.removeClan();
            removeClan(cmem);
        });
        clansById.remove(clan.getClanId());
        clansByName.remove(clan.getClanName());
        clanNames.remove(clan.getClanName());
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stmt = main.prepareStatement("DELETE FROM clans WHERE clanId=?");
                stmt.setString(1, clanId.toString());
                stmt.execute();

                stmt = main.prepareStatement("DELETE FROM clanWarps WHERE clanId=?");
                stmt.setString(1, clanId.toString());
                stmt.execute();

                stmt = main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE clanId=?");
                stmt.setString(1, clanId.toString());
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setPlayerClan(UUID uuid, Clan clan) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE players SET clanId=?, clanName=?, level=? WHERE uuid=?");
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, clan.getClanName());
                statement.setInt(3, 0);
                statement.setString(4, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setClanColor(Clan clan, String clanColor) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE clans SET clanColor=? WHERE clanId=?");
                statement.setString(1, clanColor);
                statement.setString(2, clan.getClanId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setPublicClan(Clan clan, boolean publicClan) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE clans SET publicClan=? WHERE clanId=?");
                statement.setBoolean(1, publicClan);
                statement.setString(2, clan.getClanId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setPlayerLevel(UUID uuid, Integer level) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE players SET level=? WHERE uuid=?");
                statement.setInt(1, level);
                statement.setString(2, uuid.toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void delWarp(Clan clan, String warp) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try (PreparedStatement statement = main.prepareStatement("DELETE FROM clanWarps WHERE clanId=? AND name=?")) {
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, warp);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setWarp(Clan clan, Warp warp) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try (PreparedStatement statement = main.prepareStatement("INSERT INTO clanWarps(clanId, name, levelNeeded, x, y, z, pitch, yaw, world) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, warp.name);
                statement.setInt(3, warp.level);
                statement.setDouble(4, warp.location.getX());
                statement.setDouble(5, warp.location.getY());
                statement.setDouble(6, warp.location.getZ());
                statement.setFloat(7, warp.location.getPitch());
                statement.setFloat(8, warp.location.getYaw());
                statement.setString(9, warp.location.getWorld().getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
