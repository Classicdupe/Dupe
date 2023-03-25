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
            Level 2 = Mod
            Level 3 = Admin
            Level 4 = Owner
            */
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    main.prepareStatement("CREATE TABLE IF NOT EXISTS clans(clanId VARCHAR, clanName VARCHAR, clanKills INT)").execute();
                    main.prepareStatement("CREATE TABLE IF NOT EXISTS players(uuid VARCHAR, name VARCHAR, clanId VARCHAR, clanName VARCHAR, level INT, boosts INT)").execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static YamlConfiguration getGlobalConfig() { return globalConfig; }

    public static Clan createClan(String name, Player owner) {
        String id = genClanId();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(ClassicDupe.getPlugin().getDataFolder() + "/clansData/clans/" + id + "/config.yml"));
        config.set("name", name);
        try {
            config.save(new File(ClassicDupe.getPlugin().getDataFolder() + "/clansData/clans/" + id + "/config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/clans/" + id + "/data");
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    conn.prepareStatement("CREATE TABLE warps(name VARCHAR, location VARCHAR, levelReq INT)").execute();
                    conn.prepareStatement("CREATE TABLE perks(name VARCHAR, active BOOLEAN)").execute();
                    conn.prepareStatement("CREATE TABLE players(uuid VARCHAR)").execute();
                    conn.prepareStatement("INSERT INTO players(uuid) VALUES('" + owner.getUniqueId() + "')").execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Clan clan = new Clan(id);
        ClanMember cmem = getClanMember(owner.getUniqueId());
        cmem.setClan(clan, 4);
        return clan;
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
        if(clansByName.containsKey(name)) return clansByName.get(name);
        try {
            ResultSet clans = main.prepareStatement("SELECT * FROM clans WHERE clanName='" + name + "'").executeQuery();
            if(!clans.next()) return null;
            Clan clan = new Clan(clans.getString("clanId"));
            clansByName.put(name, clan);
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

        private String clanName;

        public Clan(String id) {
            this.clanId = id;
            clanFile = new File(ClassicDupe.getPlugin().getDataFolder() + "/clansData/clans/" + id + "/config.yml");
            clanConfig = YamlConfiguration.loadConfiguration(clanFile);
            clanName = clanConfig.getString("name");
            try {
                conn = DriverManager.getConnection("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + "/clansData/clans/" + id + "/data");
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
                while(perkSet.next()) {
                    perks.put(perkSet.getString("name"), perkSet.getBoolean("active"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public String getClanId() { return this.clanId; }
        public String getClanName() { return this.clanName; }

        public void deleteClan() {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            clansById.remove(this.clanId);
            clansByName.remove(this.clanName);
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
                if(!set.next()) throw new RuntimeException("THEY ARENT IN THE DATABASE!!!");
                this.offPlayer = Bukkit.getOfflinePlayer(set.getString("uuid"));
                this.clanId = set.getString("clanId");
                this.clanName = set.getString("clanName");
                this.level = set.getInt("level");
                this.boosts = set.getInt("boosts");
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
            this.clanId = null;
            this.clanName = null;
            this.level = null;
            Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
                try {
                    main.prepareStatement("UPDATE players SET clanId=null, clanName=null, level=null WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
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
                    main.prepareStatement("UPDATE players SET clanId='" + clan.getClanId() + "', clanName='" + clan.getClanName() + "', level=" + level + " WHERE uuid='" + this.offPlayer.getUniqueId() + "'").execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
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
