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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class ClansDatabase {

    public static File dataDir;
    public static File globalConfigFile;
    public static YamlConfiguration globalConfig;
    private static Connection main;

    private static Map<String, Clan> clansById = new HashMap<>();
    private static Map<String, Clan> clansByName = new HashMap<>();
    private static Map<UUID, ClanMember> clanMembers = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        dataDir = new File(plugin.getDataFolder() + "/clansData/");
        if(!dataDir.exists()) dataDir.mkdir();
        globalConfigFile = new File(plugin.getDataFolder() + "/clansData/global.yml");
        if(!globalConfigFile.exists()) {
            try(OutputStream outputStream = new FileOutputStream(globalConfigFile)){
                IOUtils.copy(plugin.getResource("global.yml"), outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        globalConfig = YamlConfiguration.loadConfiguration(globalConfigFile);
        try {
            main = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/main");
            /*
            Level 0 = Default
            Level 1 = Vip
            Level 2 = Admin
            Level 3 = Owner
            */
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    main.prepareStatement("CREATE TABLE IF NOT EXISTS clans(clanId VARCHAR, clanName VARCHAR, clanKills INT)").execute();
                    main.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid VARCHAR, name VARCHAR, clanId VARCHAR, clanName VARCHAR, level INT, boosts INT)").execute();
                    ResultSet set = main.prepareStatement("SELECT * FROM players").executeQuery();
                    while(set.next()) {
                        System.out.println(set.getString("uuid"));
                        System.out.println(set.getString("clanName"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static YamlConfiguration getGlobalConfig() { return globalConfig; }

    public static void createClan(String name, Player owner) {
        String id = genClanId();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(ClassicDupe.getPlugin().getDataFolder() + "/clansData/clans/" + id + "/config.yml"));
        config.set("name", name);
        ClanSettings.init(config);
        try {
            config.save(new File(ClassicDupe.getPlugin().getDataFolder() + "/clansData/clans/" + id + "/config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/clans/" + id + "/data");
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    main.prepareStatement("INSERT INTO clans(clanId, clanName, clanKills) VALUES('" + id + "', '" + name + "', 0)").execute();
                    conn.prepareStatement("CREATE TABLE warps(name VARCHAR, location VARCHAR, levelReq INT)").execute();
                    conn.prepareStatement("CREATE TABLE perks(name VARCHAR, active BOOLEAN)").execute();
                    conn.prepareStatement("CREATE TABLE players(uuid VARCHAR)").execute();
                    conn.prepareStatement("INSERT INTO players(uuid) VALUES('" + owner.getUniqueId() + "')").execute();
                    Clan clan = new Clan(id);
                    ClanMember cmem = getClanMember(owner.getUniqueId());
                    cmem.setClan(clan, 3);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String genClanId() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 12;
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphaNumeric.length());
            char randomChar = alphaNumeric.charAt(index);
            sb.append(randomChar);
        }
        String randomClanId = sb.toString();
        if(getClanByID(randomClanId) != null) return genClanId();
        return randomClanId;
    }

    public static List<String> getLoadedClanNames() { return clansById.keySet().stream().toList(); }

    @Nullable
    public static Clan getClanByID(String id) {
        if(clansById.containsKey(id)) return clansById.get(id);
        try {
            ResultSet clans = main.prepareStatement("SELECT * FROM clans WHERE clanId='" + id + "'").executeQuery();
            if(!clans.next()) return null;
            Clan clan = new Clan(clans.getString("clanId"));
            clansById.put(id, clan);
            clansByName.put(clan.getClanName(), clan);
            return clan;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Clan getClanByName(String name) {
        AtomicReference<Clan> foundClan = new AtomicReference<>(null);
        clansByName.forEach((s, clan) -> { if(s.equalsIgnoreCase(name)) foundClan.set(clan); });
        if(foundClan != null) return foundClan.get();
        try {
            ResultSet clans = main.prepareStatement("SELECT * FROM clans WHERE UPPER(clanName) LIKE UPPER('" + name + "')").executeQuery();
            if(!clans.next()) return null;
            Clan clan = new Clan(clans.getString("clanId"));
            clansByName.put(clans.getString("clanName"), clan);
            clansById.put(clan.getClanId(), clan);
            return clan;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClanMember getClanMember(UUID uuid) {
        if(clanMembers.containsKey(uuid)) return clanMembers.get(uuid);
        ClanMember member = new ClanMember(uuid.toString());
        clanMembers.put(uuid, member);
        return member;
    }

    public static void createIfNotExists(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                ResultSet set = main.prepareStatement("SELECT * FROM players WHERE uuid='" + player.getUniqueId() + "'").executeQuery();
                if(!set.next()) main.prepareStatement("INSERT INTO players(uuid, name, clanId, clanName, level, boosts) VALUES('" + player.getUniqueId() + "', '" + player.getName() + "', null, null, null, 0)").execute();
                else main.prepareStatement("UPDATE players SET name='" + player.getName() + "' WHERE uuid='" + player.getUniqueId() + "'").execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class Clan {

        private String clanId;
        private Connection conn;
        private File clanFile;
        private YamlConfiguration clanConfig;

        private Map<String, Warp> warps = new HashMap<>();
        private Map<String, Boolean> perks = new HashMap<>();
        private List<UUID> clanMemberUUIDs = new ArrayList<>();

        private String clanName;
        private ClanSettings clanSettings;

        private OfflinePlayer owner;
        private List<OfflinePlayer> admins;
        private List<OfflinePlayer> vips;
        private List<OfflinePlayer> defaults;

        public Clan(String id) {
            this.clanId = id;
            clanFile = new File(ClassicDupe.getPlugin().getDataFolder() + "/clansData/clans/" + id + "/config.yml");
            clanConfig = YamlConfiguration.loadConfiguration(clanFile);
            clanName = clanConfig.getString("name");
            this.clanSettings = new ClanSettings(clanFile);
            try {
                conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/clans/" + id + "/data");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    ResultSet warpSet = conn.prepareStatement("SELECT * FROM warps").executeQuery();
                    while(warpSet.next()) {
                        warps.put(
                                warpSet.getString("name"),
                                new Warp(
                                        warpSet.getString("name"),
                                        (Location) warpSet.getObject("location"),
                                        warpSet.getInt("levelReq")
                                )
                        );
                    }
                    ResultSet perkSet = conn.prepareStatement("SELECT * FROM perks").executeQuery();
                    while(perkSet.next()) perks.put(perkSet.getString("name"), perkSet.getBoolean("active"));
                    ResultSet uuids = conn.prepareStatement("SELECT * FROM players").executeQuery();
                    while(uuids.next()) {
                        this.clanMemberUUIDs.add(UUID.fromString(uuids.getString("uuid")));
                        ResultSet set = main.prepareStatement("SELECT * FROM players WHERE uuid='" + uuids.getString("uuid") + "'").executeQuery();
                        switch(set.getInt("level")) {
                            case 0 -> this.defaults.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 1 -> this.vips.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 2 -> this.admins.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 3 -> this.owner = Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid")));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public String getClanId() { return this.clanId; }
        public String getClanName() { return this.clanName; }
        public ClanSettings getClanSettings() { return this.clanSettings; }
        public Connection getConnection() { return this.conn; }
        public List<UUID> getClanMemberUUIDs() { return this.clanMemberUUIDs; }
        public List<String> getWarpNames() { return this.warps.keySet().stream().toList(); }
        public Map<String, Warp> getWarpMap() { return this.warps; }
        public void updateClanMemberUUIDs() {
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    ResultSet uuids = conn.prepareStatement("SELECT * FROM players").executeQuery();
                    while(uuids.next()) this.clanMemberUUIDs.add(UUID.fromString(uuids.getString("uuid")));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public OfflinePlayer getClanOwner() { return this.owner; }
        public List<OfflinePlayer> getClanAdmins() { return this.admins; }
        public List<OfflinePlayer> getClanVips() { return this.vips; }
        public List<OfflinePlayer> getClanDefaults() { return this.defaults; }

        public void updatePlayers() {
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    ResultSet uuids = conn.prepareStatement("SELECT * FROM players").executeQuery();
                    while(uuids.next()) {
                        this.clanMemberUUIDs.add(UUID.fromString(uuids.getString("uuid")));
                        ResultSet set = main.prepareStatement("SELECT * FROM players WHERE uuid='" + uuids.getString("uuid") + "'").executeQuery();
                        switch(set.getInt("level")) {
                            case 0 -> this.defaults.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 1 -> this.vips.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 2 -> this.admins.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 3 -> this.owner = Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid")));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void delWarp(String name) {
            this.warps.remove(name);
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    conn.prepareStatement("DELETE FROM warps WHERE name='" + name + "'").execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void setWarp(String name, Location loc) {
            this.warps.put(name, new Warp(
                    name,
                    loc,
                    0
            ));
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    conn.prepareStatement("INSERT INTO warps(name, location, levelReq) VALUES('" + name + "', '" + loc.toString() + "', 0)").execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void deleteClan() {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            clansById.remove(this.clanId);
            clansByName.remove(this.clanName);
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    ResultSet set = main.prepareStatement("SELECT * FROM players WHERE clanId='" + this.clanId + "'").executeQuery();
                    main.prepareStatement("DELETE FROM clans WHERE clanId='" + this.clanId + "'").execute();
                    while(set.next()) clanMembers.remove(UUID.fromString(set.getString("uuid")));
                    main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=0 WHERE clanId='" + this.clanId + "'").execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Path path = Paths.get(ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/clans/" + this.clanId);
                try (Stream<Path> walk = Files.walk(path)) {
                    walk
                            .sorted(Comparator.reverseOrder())
                            .forEach(dir -> {
                                try {
                                    Files.delete(dir);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    public static class ClanSettings {

        private File clanConfigFile;
        private YamlConfiguration clanConfig;

        private boolean publicClan;
        private String clanColor;

        public ClanSettings(File configFile) {
            this.clanConfigFile = configFile;
            this.clanConfig = YamlConfiguration.loadConfiguration(this.clanConfigFile);

            this.publicClan = this.clanConfig.getBoolean("publicClan");
            this.clanColor = this.clanConfig.getString("clanColor");
        }

        public String getClanColor() { return this.clanColor; }
        public void setClanColor(String color) {
            this.clanConfig.set("clanColor", color);
            save();
        }

        public boolean getPublicClan() { return this.publicClan; }
        public void setPublicClan(boolean setting) {
            this.clanConfig.set("publicClan", setting);
            save();
        }

        public static void init(YamlConfiguration config) {
            config.set("publicClan", true);
            config.set("clanColor", "&e");
        }

        private void save() {
            try {
                this.clanConfig.save(this.clanConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static class ClanMember {

        private OfflinePlayer offPlayer;
        private String clanId;
        private String clanName;
        private Integer level;
        private Integer boosts;

        public ClanMember(String uuid) {
            try {
                ResultSet set = main.prepareStatement("SELECT * FROM players WHERE uuid='" + uuid + "'").executeQuery();
                if(set.next()) {
                    this.offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("uuid")));
                    this.clanId = set.getString("clanId");
                    this.clanName = set.getString("clanName");
                    this.level = set.getInt("level");
                    this.boosts = set.getInt("boosts");
                } else throw new RuntimeException("THEY ARENT IN THE DATABASE!!!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public OfflinePlayer getOffPlayer() { return this.offPlayer; }
        public String getClanId() { return this.clanId; }
        public String getClanName() { return this.clanName; }
        public Integer getLevel() { return this.level; }
        public Integer getBoosts() { return this.boosts; }

        public void removeClan() {
            Clan clan = getClanByID(this.clanId);
            this.clanId = null;
            this.clanName = null;
            this.level = null;
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    clan.getConnection().prepareStatement("DELETE FROM player WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                    main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                    clan.updateClanMemberUUIDs();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void setLevel(Integer level) {
            this.level = level;
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    main.prepareStatement("UPDATE players SET level=" + level + " WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                    ClansDatabase.getClanByID(this.clanId).updatePlayers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public void setClan(Clan clan, Integer level) {
            this.clanId = clan.getClanId();
            this.clanName = clan.getClanName();
            this.level = level;
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    clan.getConnection().prepareStatement("INSERT INTO players(uuid) VALUES('" + this.offPlayer.getUniqueId() + "')").execute();
                    main.prepareStatement("UPDATE players SET clanId='" + clan.getClanId() + "', clanName='" + clan.getClanName() + "', level=" + level + " WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                    clan.updateClanMemberUUIDs();
                } catch (SQLException e) {
                    Bukkit.getLogger().severe(e.toString());
                }
            });
        }

    }

    public static class Warp {
        public String name;
        public Location location;
        public Integer levelReq;
        public Warp(String name, Location loc, Integer levelReq) {
            this.name = name;
            this.location = loc;
            this.levelReq = levelReq;
        }
    }

}
