package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BountyDatabase {

    private final Connection conn;

    private final Map<UUID, Integer> bounties = new HashMap<>();

    public BountyDatabase(Connection conn) {
        this.conn = conn;
        loadAllBounties();
    }

    /**
     * Gets all bounties sorted from highest amount to lowest
     *
     * @return Sorted map of uuids and bounties
     */
    public Map<UUID, Integer> getBountiesSorted() {
        List<Map.Entry<UUID, Integer>> list = new ArrayList<>(bounties.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Map<UUID, Integer> sorted = new HashMap<>();
        for(Map.Entry<UUID, Integer> entry : list) sorted.put(entry.getKey(), entry.getValue());
        return sorted;
    }

    /**
     * Loads all the bounties into the bounties map.
     * This is done asynchronously
     */
    public void loadAllBounties() {
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                ResultSet set = conn.prepareStatement("SELECT * FROM bounties").executeQuery();
                while(set.next()) {
                    bounties.put(
                            UUID.fromString(set.getString("uuid")),
                            set.getInt("amount")
                    );
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Sets the bounty of a player
     * @param uuid UUID of player
     * @param amount Amount to set bounty to
     */
    public void setBounty(UUID uuid, Integer amount) {
        bounties.put(uuid, amount);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat  = this.conn.prepareStatement("SELECT * FROM bounties WHERE uuid=?");
                stat.setString(1, uuid.toString());
                ResultSet set = stat.executeQuery();
                if(set.next()) {
                    PreparedStatement stat2 = this.conn.prepareStatement("UPDATE bounties SET amount=? WHERE uuid=?");
                    stat2.setInt(1, amount);
                    stat2.setString(2, uuid.toString());
                    stat2.execute();
                } else {
                    PreparedStatement stat2 = this.conn.prepareStatement("INSERT INTO bounties(uuid, amount) VALUES (?, ?)");
                    stat2.setString(1, uuid.toString());
                    stat2.setInt(2, amount);
                    stat2.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Deletes a bounty placed on a player
     * @param uuid UUID of player
     */
    public void deleteBounty(UUID uuid) {
        if(bounties.containsKey(uuid)) bounties.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat  = this.conn.prepareStatement("SELECT * FROM bounties WHERE uuid=?");
                stat.setString(1, uuid.toString());
                ResultSet set = stat.executeQuery();
                if(set.next()) {
                    PreparedStatement stat2 = this.conn.prepareStatement("DELETE FROM bounties WHERE uuid=?");
                    stat2.setString(1, uuid.toString());
                    stat2.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Removes an amount of bounty from a player.
     * This is done asynchronously
     * @param uuid UUID of player
     * @param amount The amount to remove
     */
    public void removeBounty(UUID uuid, Integer amount) {
        if(bounties.containsKey(uuid)) bounties.put(uuid, bounties.get(uuid)-amount);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat  = this.conn.prepareStatement("SELECT * FROM bounties WHERE uuid=?");
                stat.setString(1, uuid.toString());
                ResultSet set = stat.executeQuery();
                if(set.next()) {
                    PreparedStatement stat2 = this.conn.prepareStatement("UPDATE bounties SET amount=amount-? WHERE uuid=?");
                    stat2.setInt(1, amount);
                    stat2.setString(2, uuid.toString());
                    stat2.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Adds an amount of bounty to a player.
     * This is done asynchronously
     * @param uuid UUID of player
     * @param amount Amount to add
     */
    public void addBounty(UUID uuid, Integer amount) {
        if(bounties.containsKey(uuid)) bounties.put(uuid, bounties.get(uuid)+amount);
        else bounties.put(uuid, amount);
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat  = this.conn.prepareStatement("SELECT * FROM bounties WHERE uuid=?");
                stat.setString(1, uuid.toString());
                ResultSet set = stat.executeQuery();
                if(set.next()) {
                    PreparedStatement stat2 = this.conn.prepareStatement("UPDATE bounties SET amount=amount+? WHERE uuid=?");
                    stat2.setInt(1, amount);
                    stat2.setString(2, uuid.toString());
                    stat2.execute();
                } else {
                    PreparedStatement stat2 = this.conn.prepareStatement("INSERT INTO bounties(uuid, amount) VALUES (?, ?)");
                    stat2.setString(1, uuid.toString());
                    stat2.setInt(2, amount);
                    stat2.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Gets the bounty of a player. Returns null if the player does not have a bounty
     * @param uuid UUID of player
     * @return The bounty of the player
     */
    @Nullable
    public Integer getBounty(UUID uuid) {
        if(bounties.containsKey(uuid)) return bounties.get(uuid);
        else {
            try {
                PreparedStatement stat = this.conn.prepareStatement("SELECT * FROM bounties WHERE uuid=?");
                stat.setString(1, uuid.toString());
                ResultSet set = stat.executeQuery();
                if(set.next()) {
                    bounties.put(uuid, set.getInt("amount"));
                    return set.getInt("amount");
                } else return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}