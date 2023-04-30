package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkingDatabase {

    final Connection conn;

    public LinkingDatabase(Connection conn) {
        this.conn = conn;
    }

    public static class Link {
        public final String uuid;
        public final Long id;
        public Link(String uuid1, Long id1) {
            uuid = uuid1;
            id = id1;
        }
    }

    public void unlinkById(Long id) {
        try {
            conn.prepareStatement("DELETE FROM link WHERE dscid=" + id + "").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void unlinkByUUID(String uuid) {
        try {
            conn.prepareStatement("DELETE FROM link WHERE uuid='" + uuid + "'").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public void setLink(String uuid, Long id) {
        try {
            conn.prepareStatement("DELETE FROM link WHERE uuid='" + uuid + "'").execute();
            conn.prepareStatement("DELETE FROM link WHERE dscid=" + id + "").execute();
            conn.prepareStatement("INSERT INTO link(uuid, dscid) VALUES('" + uuid + "', " + id + ")").execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }

    public Link getLinkFromUUID(String uuid) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM link WHERE uuid='" + uuid + "'").executeQuery();
            if(set.next()) return new Link(set.getString("uuid"), set.getLong("dscid"));
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

    public Link getLinkFromId(Long id) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM link WHERE dscid=" + id).executeQuery();
            if(set.next()) return new Link(set.getString("uuid"), set.getLong("dscid"));
            return null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
            return null;
        }
    }

}
