package xyz.prorickey.classicdupe.clans;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClansData {

    private Connection mainConn;
    private Connection clansConn;
    private Connection playersConn;

    public ClansData() {
        try {

            mainConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "clans" + File.separator + "main");
            clansConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "clans" + File.separator + "clans");
            playersConn = DriverManager.getConnection ("jdbc:h2:" + ClassicDupe.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "clans" + File.separator + "clans");

            mainConn.prepareStatement("CREATE TABLE IF NOT EXISTS clans (clanId VARCHAR, clanName VARCHAR, public BOOL)").execute();

            playersConn.prepareStatement("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR, name VARCHAR, clanId VARCHAR, boosts INT)").execute();

        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    /*
    Perm Levels
    0 - Default
    1 - Vip
    2 - Mod
    3 - Admin
    4 - Owner

    Main File contains clan settings
    Clans file contains clan members
    Players file contains player data

     */

    public class Clan {

        private final String clanId;
        private final List<ClanMember> members = new ArrayList<>();
        private Integer boosts;

        public Clan(String clanId1) {
            clanId = clanId1;
            try {
                ResultSet clans = clansConn.prepareStatement("SELECT * FROM clan_" + clanId).executeQuery();
                while(clans.next()) {
                    ResultSet players = playersConn.prepareStatement("SELECT * FROM players WHERE uuid='" + clans.getString("uuid") + "'").executeQuery();
                    members.add(
                            new ClanMember(
                                    clans.getString("uuid"),
                                    clans.getInt("permLevel"),
                                    clans.getLong("joined"),
                                    clans.getInt("clanKills")
                            )
                    );
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }

        public void removeFromClan(String uuid) {
            try {
                clansConn.prepareStatement("DELETE FROM clan_" + clanId + " WHERE uuid='" + uuid + "'").execute();
                playersConn.prepareStatement("UPDATE players SET clanId=null WHERE uuid='" + uuid + "'").execute();
                for(int i = 0; i < members.size(); i++) {
                    ClanMember mem = members.get(i);
                    if(mem.getUUID().equals(uuid)) members.remove(mem);
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }

        public void addToClan(Player player) {
            try {
                clansConn.prepareStatement("INSERT INTO clan_" + clanId + "(uuid, permLevel, name, joined, clanKills) VALUES('" + player.getUniqueId() + "', 0, '" + player.getName() + "', " + System.currentTimeMillis() + ", 0)").execute();
                members.add(new ClanMember(
                        player.getUniqueId().toString(),
                        0,
                        System.currentTimeMillis(),
                        0
                ));
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }

        public void deleteClan() {
            try {
                clansConn.prepareStatement("DROP TABLE clan_" + clanId).execute();
                mainConn.prepareStatement("DELETE FROM clans WHERE clanId='" + clanId + "'").execute();
                playersConn.prepareStatement("UPDATE players SET clanId=null WHERE clanId='" + clanId + "'").execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }

        public String getClanID() { return clanId; }
        public List<ClanMember> getMembers() { return members; }

    }

    public class ClanMember {

        private final String uuid;
        private String name;
        private String clanId;
        private String clanName;
        private final Integer permLevel;
        private final Long joined;
        private final Integer clanKills;
        private Integer boosts;

        public ClanMember(String uuid1, Integer permLevel1, Long joined1, Integer clanKills1) {
            uuid = uuid1;
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
            permLevel = permLevel1;
            joined = joined1;
            clanKills = clanKills1;
            try {
                ResultSet players = playersConn.prepareStatement("SELECT * FROM players WHERE uuid='" + uuid + "'").executeQuery();
                if(players.next()) {
                    name = players.getString("name");
                    clanId = players.getString("clanId");
                    boosts = players.getInt("boosts");
                }
                if(clanId != null) {
                    ResultSet main = mainConn.prepareStatement("SELECT * FROM clans WHERE clanId='" + clanId + "'").executeQuery();
                    if(main.next()) {
                        clanName = main.getString("clanName");
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }

        public String getUUID() { return uuid; }
        public String getName() { return name; }
        public String getClanID() { return clanId; }
        public String getClanName() { return clanName; }
        public Integer getPermLevel() { return permLevel; }
        public Long getJoined() { return joined; }
        public Integer getClanKills() { return clanKills; }
        public Integer getBoosts() { return boosts; }

    }

    @Nullable
    public Clan getClanByName(String name) {
        try {
            ResultSet clans = mainConn.prepareStatement("SELECT * FROM clans WHERE UPPER(clanName) LIKE UPPER('" + name + "')").executeQuery();
            if(clans.next()) return new Clan(clans.getString("clanId"));
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return null;
    }

    @Nullable
    public ClanMember getPlayer(String uuid) {
        try {
            ResultSet players = playersConn.prepareStatement("SELECT * FROM players WHERE uuid='" + uuid + "'").executeQuery();
            if(players.next()) {
                if(players.getString("clanId") != null) {
                    ResultSet clanPlayer = clansConn.prepareStatement("SELECT * FROM clan_" + players.getString("clanId") + " WHERE uuid='" + uuid + "'").executeQuery();
                    return new ClanMember(
                            uuid,
                            clanPlayer.getInt("permLevel"),
                            clanPlayer.getLong("joined"),
                            clanPlayer.getInt("clanKills")
                    );
                } else {
                    return new ClanMember(uuid, null, null, null);
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return null;
    }

    public boolean createClan(String name, Player owner) {
        String id = genIdForClan();
        if(id == null) return false;
        try {
            ResultSet set = mainConn.prepareStatement("SELECT * FROM clans WHERE UPPER(clanName) LIKE UPPER('" + name + "')").executeQuery();
            if(set.next()) return false;
            mainConn.prepareStatement("INSERT INTO clans(clanId, clanName, public) VALUES('" + id + "', '" + name + "', FALSE)").execute();
            clansConn.prepareStatement("CREATE TABLE IF NOT EXISTS clan_" + id + "(uuid VARCHAR, permLevel INT, name VARCHAR, joined BIGINT, clanKills INT)").execute();

            ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(owner.getUniqueId().toString());

            clansConn.prepareStatement("INSERT INTO clan_" + id + "(uuid, permLevel, name, joined, clanKills) VALUES('" + owner.getUniqueId() + "', 4, '" + owner.getName() + "', " + System.currentTimeMillis() + ", 0)").execute();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return false;
    }

    @Nullable
    public String genIdForClan() {
        String id = RandomStringUtils.randomAlphabetic(25);
        try {
            ResultSet set = mainConn.prepareStatement("SELECT * FROM clans WHERE clanId='" + id + "'").executeQuery();
            if(set.next()) return genIdForClan();
            return id;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return null;
    }

    public void joinEventClansUpdate(Player player) {
        try {
            ResultSet players = playersConn.prepareStatement("SELECT * FROM players WHERE uuid='" + player.getUniqueId() + "'").executeQuery();
            if(players.next()) {
                playersConn.prepareStatement("UPDATE players SET name='" + player.getName() + "'" + " WHERE uuid='" + player.getUniqueId() + "'").execute();
                if(players.getString("clanId") != null) clansConn.prepareStatement("UPDATE clan_" + players.getString("clanId") + " SET name='" + player.getName() + "' WHERE uuid='" + player.getUniqueId() + "'").execute();
            } else {
                playersConn.prepareStatement("INSERT INTO players(uuid, name, clanId, boosts) VALUES('" + player.getUniqueId() + "', '" + player.getName() + "', null, 0)").execute();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }

    }

}
