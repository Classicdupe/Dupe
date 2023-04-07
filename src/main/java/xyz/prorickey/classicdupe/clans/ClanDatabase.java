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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClanDatabase {

    public static File dataDir;
    public static File globalConfigFile;
    public static YamlConfiguration globalConfig;
    private static Connection main;

    private static List<String> clanNames = new ArrayList<>();
    private static Map<UUID, Clan> clansById = new HashMap<>();
    private static Map<String, Clan> clansByName = new HashMap<>();
    private static Map<UUID, ClanMember> clanMembers = new HashMap<>();

    public void init(JavaPlugin plugin) {
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                main = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/main");
                // x y z pitch yaw world
                main.prepareStatement("CREATE TABLE IF NOT EXISTS clans(clanId VARCHAR, clanName VARCHAR, clanKills INT, publicClan BOOLEAN, clanColor VARCHAR)").execute();
                main.prepareStatement("CREATE TABLE IF NOT EXISTS clanWarps(clanId VARCHAR, name VARCHAR, loc VARCHAR, levelNeeded INT)").execute();

                main.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid VARCHAR, name VARCHAR, clanId VARCHAR, clanName VARCHAR, level INT, boosts INT)").execute();

                ResultSet clanSet = main.prepareStatement("SELECT * FROM clans").executeQuery();
                while(clanSet.next()) {
                    Clan clan = new Clan(UUID.fromString(clanSet.getString("clanId")), clanSet.getString("clanName"));
                    clan.setClanColor(clanSet.getString("clanColor"));
                    clan.setPublicClan(clanSet.getBoolean("publicClan"));
                }

                ResultSet warpSet = main.prepareStatement("SELECT * FROM clanWarps").executeQuery();
                while(warpSet.next()) {
                    Clan clan = clansById.get(UUID.fromString(warpSet.getString("clanId")));
                    String[] str = warpSet.getString("loc").split(",");
                    Location loc = new Location(Bukkit.getWorld(str[5]), Integer.getInteger(str[0]), Integer.getInteger(str[1]), Integer.getInteger(str[2]), Float.parseFloat(str[3]), Float.parseFloat(str[4]));
                    clan.setWarp(new Warp(warpSet.getString("name"), loc, warpSet.getInt("levelNeeded")));
                }

                ResultSet players = main.prepareStatement("SELECT * FROM players").executeQuery();
                while(players.next()) {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(players.getString("uuid")));
                    ClanMember cmem = new ClanMember(offPlayer);
                    if(players.getString("clanId") != null) {
                        Clan clan = clansById.get(UUID.fromString(players.getString("clanId")));
                        switch(players.getInt("level")) {
                            case 0 -> clan.addDefault(offPlayer);
                            case 1 -> clan.addVip(offPlayer);
                            case 2 -> clan.addAdmin(offPlayer);
                            case 3 -> clan.setOwner(offPlayer);
                        }
                        cmem.setClan(clan, players.getInt("level"));
                    }
                    clanMembers.put(offPlayer.getUniqueId(), cmem);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static YamlConfiguration getGlobalConfig() { return globalConfig; }

    public static void createClan(String name, Player owner) {
        String id = genClanId();
        Clan clan = new Clan(UUID.fromString(id), name);
        clan.setOwner(owner);
        ClanMember cmem = clanMembers.get(owner.getUniqueId());
        cmem.setClan(clan, 3);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                main.prepareStatement("INSERT INTO clans(clanId, clanName, clanKills, publicClan, clanColor) VALUES('" + id + "', '" + name + "', 0, false, '&e')").execute();
                main.prepareStatement("UPDATE players SET clanId='" + id + "', clanName='" + name + "', level=3 WHERE uuid='" + owner.getUniqueId() + "'").execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
        if(getClan(UUID.fromString(randomClanId)) != null) return genClanId();
        return randomClanId;
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
            try {
                main.prepareStatement("INSERT INTO players(uuid, name, clanId, clanName, level, boosts) VALUES('" + player.getUniqueId() + "', '" + player.getName() + "', null, null, null, 0)").execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void deleteClan(Clan clan) {
        UUID clanId = clan.getClanId();
        clanMembers.forEach((uuid, cmem) -> { if(cmem.getClanID() == clanId) cmem.removeClan(); });
        clansById.remove(clan.getClanId());
        clansByName.remove(clan.getClanName());
        clanNames.remove(clan.getClanName());
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                main.prepareStatement("DELETE FROM clans WHERE clanId='" + clanId + "'").execute();
                main.prepareStatement("DELETE FROM clanWarps WHERE clanId='" + clanId + "'").execute();
                main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE clanId='" + clanId + "'").execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setPlayerClan(UUID uuid, Clan clan) {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                main.prepareStatement("UPDATE players SET clanId='" + clan.getClanId() + "', clanName='" + clan.getClanName() + "', level=3 WHERE uuid='" + uuid + "'").execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /*public static class Clan {

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
        private List<OfflinePlayer> admins = new ArrayList<>();
        private List<OfflinePlayer> vips = new ArrayList<>();
        private List<OfflinePlayer> defaults = new ArrayList<>();

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
                        if(set.next()) switch(set.getInt("level")) {
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

        public OfflinePlayer getClanOwner() { return this.owner; }
        public List<OfflinePlayer> getClanAdmins() { return this.admins; }
        public List<OfflinePlayer> getClanVips() { return this.vips; }
        public List<OfflinePlayer> getClanDefaults() { return this.defaults; }

        public void updatePlayers() {
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    this.defaults = new ArrayList<>();
                    this.vips = new ArrayList<>();
                    this.admins = new ArrayList<>();
                    this.owner = null;
                    this.clanMemberUUIDs = new ArrayList<>();
                    ResultSet uuids = conn.prepareStatement("SELECT * FROM players").executeQuery();
                    while(uuids.next()) {
                        this.clanMemberUUIDs.add(UUID.fromString(uuids.getString("uuid")));
                        ResultSet set = main.prepareStatement("SELECT * FROM players WHERE uuid='" + uuids.getString("uuid") + "'").executeQuery();
                        set.next();
                        switch(set.getInt("level")) {
                            case 0 -> this.defaults.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 1 -> this.vips.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 2 -> this.admins.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid"))));
                            case 3 -> this.owner = Bukkit.getOfflinePlayer(UUID.fromString(uuids.getString("uuid")));
                        }
                        System.out.println(this.defaults);
                        System.out.println(this.vips);
                        System.out.println(this.admins);
                        System.out.println(this.owner);
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
            this.clanColor = color;
            this.clanConfig.set("clanColor", color);
            save();
        }

        public boolean getPublicClan() { return this.publicClan; }
        public void setPublicClan(boolean setting) {
            this.publicClan = setting;
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
                    clan.getConnection().prepareStatement("DELETE FROM players WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                    main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                    clan.updatePlayers();
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
                    ClanDatabase.getClanByID(this.clanId).updatePlayers();
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
                    clan.updatePlayers();
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
    }*/

}
