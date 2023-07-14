package xyz.prorickey.classicdupe.clans.databases;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.h2.util.IOUtils;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.clans.builders.Warp;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MariaClanDatabase implements ClanDatabase {

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
                throw new RuntimeException(e);
            }
        }

        globalConfig = YamlConfiguration.loadConfiguration(globalConfigFile);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS clans(clanId TEXT, clanName TEXT, clanKills INT, publicClan BOOLEAN, clanColor TEXT)").execute();
                this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS clanWarps(clanId TEXT, name TEXT, levelNeeded INT, x INT, y INT, z INT, pitch FLOAT, yaw FLOAT, world TEXT)").execute();
                this.conn.prepareStatement("CREATE TABLE IF NOT EXISTS clanPlayers(uuid TEXT, name TEXT, clanId TEXT, clanName TEXT, level INT, boosts INT)").execute();

                ResultSet data = this.conn.prepareStatement("SELECT clanId, clanName FROM clans").executeQuery();
                while(data.next()) {
                    PreparedStatement stat = this.conn.prepareStatement("SELECT uuid FROM clanPlayers WHERE clanId=?");
                    stat.setString(1, data.getString("clanId"));
                    ResultSet players = stat.executeQuery();
                    if(players.next()) {
                        this.allClanIds.add(UUID.fromString(data.getString("clanId")));
                        this.allClanNames.add(data.getString("clanName"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        new TopClanKills().runTaskTimerAsynchronously(plugin, 0, 20 * 60);
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
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void deleteClan(Clan clan) {
        loadedClanMembers
                .values()
                .stream()
                .filter(cmem -> cmem.getClanID() != null)
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

                stmt = this.conn.prepareStatement("UPDATE clanPlayers SET clanId=null, clanName=null, level=null WHERE clanId=?");
                stmt.setString(1, clan.getClanId().toString());
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<String> getLoadedClanNames() {
        return allClanNames;
    }

    @Override
    @Nullable
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
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    @Override
    @Nullable
    public Clan getClan(String clanName) {
        AtomicReference<Clan> returnClan = new AtomicReference<>(null);
        loadedClans.values().stream().filter(clan -> clan.getClanName().equalsIgnoreCase(clanName)).findFirst().ifPresentOrElse(returnClan::set, () -> {
            try {
                PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM clans WHERE clanName=?");
                stmt.setString(1, clanName);
                ResultSet data = stmt.executeQuery();
                if(data.next()) {
                    Clan clan = new Clan(UUID.fromString(data.getString("clanId")), clanName);
                    clan.setClanColor(data.getString("clanColor"));
                    clan.setPublicClan(data.getBoolean("publicClan"));
                    clan.setClanKills(data.getInt("clanKills"));
                    PreparedStatement stmt2 = this.conn.prepareStatement("SELECT uuid, level FROM clanPlayers WHERE clanId=?");
                    stmt2.setString(1, data.getString("clanId"));
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
                    loadedClans.put(UUID.fromString(data.getString("clanId")), clan);
                    returnClan.set(clan);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return returnClan.get();
    }

    @Override
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public ClanMember getClanMember(UUID uuid) {
        if(loadedClanMembers.containsKey(uuid)) return loadedClanMembers.get(uuid);
        else {
            try {
                PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM clanPlayers WHERE uuid=?");
                stmt.setString(1, uuid.toString());
                ResultSet data = stmt.executeQuery();
                if(data.next()) {
                    ClanMember clanMember = new ClanMember(Bukkit.getOfflinePlayer(uuid));
                    if(data.getString("clanId") != null) clanMember
                            .setClan(getClan(UUID.fromString(data.getString("clanId"))), data.getInt("level"));
                    loadedClanMembers.put(uuid, clanMember);
                    return clanMember;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void updateClanMemberInfo(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = this.conn.prepareStatement("SELECT * FROM clanPlayers WHERE uuid=?");
                stat.setString(1, player.getUniqueId().toString());
                ResultSet data = stat.executeQuery();
                if(data.next()) {
                    PreparedStatement stmt = this.conn.prepareStatement("UPDATE clanPlayers SET name=? WHERE uuid=?");
                    stmt.setString(1, player.getName());
                    stmt.setString(2, player.getUniqueId().toString());
                    stmt.execute();
                    ClanMember clanMember = new ClanMember(player);
                    if(data.getString("clanId") != null) clanMember
                            .setClan(getClan(UUID.fromString(data.getString("clanId"))), data.getInt("level"));
                    loadedClanMembers.put(player.getUniqueId(), clanMember);
                } else {
                    PreparedStatement stmt = this.conn.prepareStatement("INSERT INTO clanPlayers (uuid, name, clanId, clanName, level, boosts) VALUES (?, ?, null, null, null, 0)");
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, player.getName());
                    stmt.execute();
                    loadedClanMembers.put(player.getUniqueId(), new ClanMember(player));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setClan(UUID uuid, Clan clan) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = this.conn.prepareStatement("UPDATE clanPlayers SET clanId=?, clanName=?, level=0 WHERE uuid=?");
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, clan.getClanName());
                statement.setString(3, uuid.toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void removeClan(ClanMember clanMember) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stmt = this.conn.prepareStatement("UPDATE clanPlayers SET clanId=null, clanName=null, level=null WHERE uuid=?");
                stmt.setString(1, clanMember.getOffPlayer().getUniqueId().toString());
                stmt.execute();
                if(clanChatMembers.contains(clanMember.getOffPlayer().getPlayer())) removeFromClanChat(clanMember.getOffPlayer().getPlayer());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setClanColor(Clan clan, String color) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                if(loadedClans.containsKey(clan.getClanId())) loadedClans.get(clan.getClanId()).setClanColor(color);
                PreparedStatement statement = this.conn.prepareStatement("UPDATE clans SET clanColor=? WHERE clanId=?");
                statement.setString(1, color);
                statement.setString(2, clan.getClanId().toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setPublicClan(Clan clan, boolean isPublic) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                if(loadedClans.containsKey(clan.getClanId())) loadedClans.get(clan.getClanId()).setPublicClan(isPublic);
                PreparedStatement statement = this.conn.prepareStatement("UPDATE clans SET publicClan=? WHERE clanId=?");
                statement.setBoolean(1, isPublic);
                statement.setString(2, clan.getClanId().toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setWarp(Clan clan, Warp warp) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            if(loadedClans.containsKey(clan.getClanId())) loadedClans.get(clan.getClanId()).setWarp(warp);
            try (PreparedStatement statement = this.conn.prepareStatement("INSERT INTO clanWarps(clanId, name, levelNeeded, x, y, z, pitch, yaw, world) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, warp.name);
                statement.setInt(3, warp.level);
                statement.setDouble(4, warp.location.getX());
                statement.setDouble(5, warp.location.getY());
                statement.setDouble(6, warp.location.getZ());
                statement.setFloat(7, warp.location.getPitch());
                statement.setFloat(8, warp.location.getYaw());
                statement.setString(9, warp.location.getWorld().getName());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void delWarp(Clan clan, String warpName) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            if(loadedClans.containsKey(clan.getClanId())) loadedClans.get(clan.getClanId()).delWarp(warpName);
            try (PreparedStatement statement = this.conn.prepareStatement("DELETE FROM clanWarps WHERE clanId=? AND name=?")) {
                statement.setString(1, clan.getClanId().toString());
                statement.setString(2, warpName);
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void setPlayerLevel(ClanMember clanMember, int level) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            if(loadedClanMembers.containsKey(clanMember.getOffPlayer().getUniqueId())) loadedClanMembers.get(clanMember.getOffPlayer().getUniqueId()).setLevel(level);
            if(loadedClans.containsKey(clanMember.getClanID())) {
                Clan clan = loadedClans.get(clanMember.getClanID());
                clan.removeAdmin(clanMember.getOffPlayer());
                clan.removeVip(clanMember.getOffPlayer());
                clan.removeDefault(clanMember.getOffPlayer());
                switch(level) {
                    case 0 -> clan.addDefault(clanMember.getOffPlayer());
                    case 1 -> clan.addVip(clanMember.getOffPlayer());
                    case 2 -> clan.addAdmin(clanMember.getOffPlayer());
                    case 3 -> clan.setOwner(clanMember.getOffPlayer());
                }
            }
            try {
                PreparedStatement statement = this.conn.prepareStatement("UPDATE clanPlayers SET level=? WHERE uuid=?");
                statement.setInt(1, level);
                statement.setString(2, clanMember.getOffPlayer().getUniqueId().toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void putInClanChat(Player player) { clanChatMembers.add(player); }

    @Override
    public void removeFromClanChat(Player player) { clanChatMembers.remove(player); }

    @Override
    public boolean clanChat(Player player) { return clanChatMembers.contains(player); }

    @Override
    public void sendClanChat(String message, Player player) {
        ClanMember cmem = getClanMember(player.getUniqueId());
        Clan clan = getClan(cmem.getClanID());
        MiniMessage mm = MiniMessage.miniMessage();
        clan.getMembers().forEach(mem -> {
            if(mem.isOnline()) mem.getPlayer().sendMessage(
                    Utils.format("<dark_gray>[<yellow>CLANCHAT<dark_gray>] ")
                            .append(Utils.format("<yellow>" + player.getName() + " <dark_gray>\u00BB <white>" +
                                    mm.stripTags(message))));
        });
    }

    @Override
    public void addClanKill(Clan clan) {
        clan.setClanKills(clan.getClanKills()+1);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = this.conn.prepareStatement("UPDATE clans SET clanKills=clanKills+1 WHERE clanId=?");
                statement.setString(1, clan.getClanId().toString());
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Map<Integer, Clan> topClanKills = new HashMap<>();

    @Override
    public Map<Integer, Clan> getTopClanKills() { return topClanKills; }

    public class TopClanKills extends BukkitRunnable {
        @Override
        public void run() {
            try {
                Map<Integer, Clan> clanKillsSorted = new HashMap<>();
                ResultSet rs = conn.prepareStatement("SELECT * FROM clans ORDER BY clanKills DESC LIMIT 10").executeQuery();
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
