package xyz.prorickey.classicdupe.clans.databases;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.util.IOUtils;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.clans.ClanDatabaseIml;
import xyz.prorickey.classicdupe.clans.Warp;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MariaClanDatabase implements ClanDatabaseIml {

    private final JavaPlugin plugin;
    private final Connection conn;

    private final File dataDir;
    private final File globalConfigFile;
    private YamlConfiguration globalConfig;

    private final List<String> allClanNames = new ArrayList<>();
    private final List<UUID> allClanIds = new ArrayList<>();

    private final Map<UUID, Clan> loadedClans = new HashMap<>();
    private final Map<UUID, ClanMember> loadedClanMembers = new HashMap<>();

    private final List<Player> clanChatMembers = new ArrayList<>();

    public MariaClanDatabase(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;

        this.dataDir = new File(plugin.getDataFolder() + "/clansData/");
        this.dataDir.mkdirs();
        this.globalConfigFile = new File(plugin.getDataFolder() + "/clansData/global.yml");
        if(!this.globalConfigFile.exists()) {
            try(InputStream inputStream = plugin.getResource("global.yml");
                OutputStream outputStream = new FileOutputStream(this.globalConfigFile)) {
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS clans(clanId TEXT, clanName TEXT, clanKills INT, publicClan BOOLEAN, clanColor TEXT)").execute();
                this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS clanWarps(clanId TEXT, name TEXT, levelNeeded INT, x INT, y INT, z INT, pitch FLOAT, yaw FLOAT, world TEXT)").execute();
                this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS clanPlayers(uuid TEXT, name TEXT, clanId TEXT, clanName TEXT, level INT, boosts INT)").execute();

                ResultSet data = this.conn.prepareStatement("SELECT clanId, clanName FROM clans").executeQuery();
                while(data.next()) {
                    this.allClanIds.add(UUID.fromString(data.getString("clanId")));
                    this.allClanNames.add(data.getString("clanName"));
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    @Override
    public YamlConfiguration getClanConfig() {
        return this.globalConfig;
    }

    @Override
    public UUID generateClanId() {
        UUID uuid = UUID.randomUUID();
        if(allClanIds.contains(uuid)) return generateClanId();
        return uuid;
    }

    @Override
    public void createClan(String clanName, Player player) {
        UUID id = generateClanId();
        Clan clan = new Clan(id, clanName);
        clan.setOwner(player);
        clan.addPlayer(player);
        ClanMember cmem = getClanMember(player.getUniqueId());
        cmem.setClan(clan, 3);
        setClan(player.getUniqueId(), clan);
        setPlayerLevel(cmem, 3);
        allClanIds.add(id);
        allClanNames.add(clanName);
        loadedClans.put(id, clan);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement insertClanStmt = this.conn.prepareStatement("INSERT INTO clans(clanId, clanName, clanKills, publicClan, clanColor) VALUES(?, ?, 0, false, '<yellow>')");
                insertClanStmt.setString(1, id.toString());
                insertClanStmt.setString(2, clanName);
                insertClanStmt.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    @Override
    public void deleteClan(Clan clan) {
        loadedClanMembers
                .values()
                .stream()
                .filter(cmem -> cmem.getClanID().equals(clan.getClanId()))
                .forEach(cmem -> {
                    cmem.removeClan();
                    removeClan(cmem);
                });
        allClanIds.remove(clan.getClanId());
        allClanNames.remove(clan.getClanName());
        loadedClans.remove(clan.getClanId());

        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stmt = this.conn.prepareStatement("DELETE FROM clans WHERE clanId=?");
                stmt.setString(1, clan.getClanId().toString());
                stmt.execute();

                stmt = this.conn.prepareStatement("DELETE FROM clanWarps WHERE clanId=?");
                stmt.setString(1, clan.getClanId().toString());
                stmt.execute();

                stmt = this.conn.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE clanId=?");
                stmt.setString(1, clan.getClanId().toString());
                stmt.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    @Override
    public List<String> getLoadedClanNames() {
        return allClanNames;
    }

    @Override
    public Clan getClan(UUID id) {
        if(loadedClans.containsKey(id)) return loadedClans.get(id);
        else {
            try {
                PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM clans WHERE clanId=?");
                stmt.setString(1, id.toString());
                ResultSet data = stmt.executeQuery();
                if(data.next()) {
                    String clanName = data.getString("clanName");
                    Clan clan = new Clan(id, clanName);
                    clan.setClanColor(data.getString("clanColor"));
                    clan.setPublicClan(data.getBoolean("publicClan"));
                    PreparedStatement stmt2 = this.conn.prepareStatement("SELECT uuid, level FROM clanPlayers WHERE clanId=?");
                    stmt2.setString(1, id.toString());
                    ResultSet data2 = stmt2.executeQuery();
                    while(data2.next()) {
                        UUID uuid = UUID.fromString(data2.getString("uuid"));
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(uuid);
                        clan.addPlayer(offPlayer);
                        switch(data2.getInt("level")) {
                            case 0 -> clan.addDefault(offPlayer);
                            case 1 -> clan.addVip(offPlayer);
                            case 2 -> clan.addAdmin(offPlayer);
                            case 3 -> clan.setOwner(offPlayer);
                        }
                    }
                    loadedClans.put(id, clan);
                    return clan;
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
            return null;
        }
    }

    @Override
    public Clan getClan(String clanName) {
        return null;
    }

    @Override
    public ClanMember getClanMember(UUID id) {
        return null;
    }

    @Override
    public void updateClanMemberInfo(Player player) {

    }

    @Override
    public void setClan(UUID uuid, Clan clan) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = this.conn.prepareStatement("UPDATE players SET clanId=?, clanName=?, level=? WHERE uuid=?");
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

    }

    @Override
    public void setClanColor(Clan clan, String color) {

    }

    @Override
    public void setPublicClan(Clan clan, boolean isPublic) {

    }

    @Override
    public void setWarp(Clan clan, Warp warp) {

    }

    @Override
    public void delWarp(Clan clan, String warpName) {

    }

    @Override
    public void setPlayerLevel(ClanMember clanMember, int level) {

    }

    @Override
    public void putInClanChat(Player player) {

    }

    @Override
    public void removeFromClanChat(Player player) {

    }

    @Override
    public boolean clanChat(Player player) {
        return false;
    }

    @Override
    public void sendClanChat(String message, Player player) {

    }
}
