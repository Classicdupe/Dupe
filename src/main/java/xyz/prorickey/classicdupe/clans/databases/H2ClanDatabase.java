package xyz.prorickey.classicdupe.clans.databases;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.h2.util.IOUtils;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.builders.Warp;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class H2ClanDatabase implements ClanDatabase {

    public static File dataDir;
    public static File globalConfigFile;
    public static YamlConfiguration globalConfig;
    private static Connection main;

    private static final List<String> clanNames = new ArrayList<>();
    private static final Map<UUID, Clan> clansById = new HashMap<>();
    private static final Map<String, Clan> clansByName = new HashMap<>();
    private static final Map<UUID, ClanMember> clanMembers = new HashMap<>();

    public static final List<Player> clanChatMembers = new ArrayList<>();

    private static Map<Integer, Clan> topClanKills = new HashMap<>();

    public H2ClanDatabase(JavaPlugin plugin) {

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
        try {
            main = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/main");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
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
                        clan.setClanKills(clanSet.getInt("clanKills"));
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
                                    PreparedStatement stat1 = main.prepareStatement("DELETE FROM clanWarps WHERE clanId=?");
                                    stat1.setString(1, clan.getClanId().toString());
                                    stat1.execute();
                                    PreparedStatement stat2 = main.prepareStatement("DELETE FROM clans WHERE clanId=?");
                                    stat2.setString(1, clan.getClanId().toString());
                                    stat2.execute();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        new TopClanKills().runTaskTimerAsynchronously(ClassicDupe.getPlugin(), 0, 20 * 60);

    }

    @Override
    public YamlConfiguration getClanConfig() {
        return globalConfig;
    }

    @Override
    public UUID generateClanId() {
        UUID uuid = UUID.randomUUID();
        if(getClan(uuid) != null) return generateClanId();
        return uuid;
    }

    @Override
    public void createClan(String clanName, Player player) {
        UUID id = generateClanId();
        Clan clan = new Clan(id, clanName);
        clan.setOwner(player);
        clan.addPlayer(player);
        ClanMember cmem = clanMembers.get(player.getUniqueId());
        cmem.setClan(clan, 3);
        clansById.put(id, clan);
        clansByName.put(clan.getClanName(), clan);
        clanNames.add(clan.getClanName());
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement insertClanStmt = main.prepareStatement("INSERT INTO clans(clanId, clanName, clanKills, publicClan, clanColor) VALUES(?, ?, 0, false, '<yellow>')");
                insertClanStmt.setString(1, id.toString());
                insertClanStmt.setString(2, clanName);
                insertClanStmt.addBatch();

                PreparedStatement updatePlayerStmt = main.prepareStatement("UPDATE players SET clanId=?, clanName=?, level=3 WHERE uuid=?");
                updatePlayerStmt.setString(1, id.toString());
                updatePlayerStmt.setString(2, clanName);
                updatePlayerStmt.setString(3, player.getUniqueId().toString());
                updatePlayerStmt.addBatch();

                insertClanStmt.executeBatch();
                updatePlayerStmt.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void deleteClan(Clan clan) {
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

    @Override
    public List<String> getLoadedClanNames() {
        return clanNames;
    }

    @Override
    public Clan getClan(UUID id) {
        return clansById.get(id);
    }

    @Override
    public Clan getClan(String clanName) {
        AtomicReference<Clan> foundClan = new AtomicReference<>(null);
        clansByName.forEach((s, clan) -> { if(s.equalsIgnoreCase(clanName)) foundClan.set(clan); });
        return foundClan.get();
    }

    @Override
    public ClanMember getClanMember(UUID id) {
        return clanMembers.get(id);
    }

    @Override
    public void updateClanMemberInfo(Player player) {
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

    @Override
    public void setClan(UUID uuid, Clan clan) {
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

    @Override
    public void removeClan(ClanMember clanMember) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE uuid='" + clanMember.getOffPlayer().getUniqueId() + "'").execute();
                if(clanChatMembers.contains(clanMember.getOffPlayer().getPlayer())) ClassicDupe.getClanDatabase().removeFromClanChat(clanMember.getOffPlayer().getPlayer());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setClanColor(Clan clan, String color) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE clans SET clanColor=? WHERE clanId=?");
                statement.setString(1, color);
                statement.setString(2, clan.getClanId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setPublicClan(Clan clan, boolean isPublic) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE clans SET publicClan=? WHERE clanId=?");
                statement.setBoolean(1, isPublic);
                statement.setString(2, clan.getClanId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setWarp(Clan clan, Warp warp) {
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

    @Override
    public void delWarp(Clan clan, String warpName) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try (PreparedStatement statement = main.prepareStatement("DELETE FROM clanWarps WHERE clanId=? AND name=?")) {
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, warpName);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setPlayerLevel(ClanMember clanMember, int level) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE players SET level=? WHERE uuid=?");
                statement.setInt(1, level);
                statement.setString(2, clanMember.getOffPlayer().getUniqueId().toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void putInClanChat(Player player) {
        clanChatMembers.add(player);
    }

    @Override
    public void removeFromClanChat(Player player) {
        clanChatMembers.remove(player);
    }

    @Override
    public boolean clanChat(Player player) {
        return clanChatMembers.contains(player);
    }

    @Override
    public void sendClanChat(String message, Player player) {
        ClanMember cmem = clanMembers.get(player.getUniqueId());
        Clan clan = clansById.get(cmem.getClanID());
        MiniMessage mm = MiniMessage.miniMessage();
        clan.getMembers().forEach(mem -> {
            if(mem.isOnline()) mem.getPlayer().sendMessage(
                    Utils.format("<dark_gray>[<yellow>CLANCHAT<dark_gray>] ")
                            .append(Utils.format("<yellow>" + player.getName() + " <dark_gray>» <white>" +
                                    mm.stripTags(message))));
        });
    }

    @Override
    public void addClanKill(Clan clan) {
        clan.setClanKills(clan.getClanKills()+1);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = main.prepareStatement("UPDATE clans SET clanKills=clanKills+1 WHERE clanId=?");
                statement.setString(1, clan.getClanId().toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Map<Integer, Clan> getTopClanKills() { return topClanKills; }

    public class TopClanKills extends BukkitRunnable {
        @Override
        public void run() {
            try {
                Map<Integer, Clan> clanKillsSorted = new HashMap<>();
                ResultSet rs = main.prepareStatement("SELECT * FROM clans ORDER BY clanKills DESC LIMIT 10").executeQuery();
                int i = 1;
                while(rs.next()) {
                    clanKillsSorted.put(i, getClan(UUID.fromString(rs.getString("clanId"))));
                    i++;
                }
                topClanKills = clanKillsSorted;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
