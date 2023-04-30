package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FilterDatabase {

    final List<FilterWord> filter = new ArrayList<>();
    final Connection conn;

    public FilterDatabase(Connection conn) {
        this.conn = conn;
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM filter").executeQuery();
            while(set.next()) {
                filter.add(
                        new FilterWord(
                                set.getString("text"),
                                set.getBoolean("fullword")
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addWordToFilter(String text, Boolean fullword) {
        try {
            if(filterWordExists(text)) return false;
            PreparedStatement stat = conn.prepareStatement("INSERT INTO filter(text, fullword) VALUES (?, ?)");
            stat.setString(1, text);
            stat.setBoolean(2, fullword);
            stat.execute();
            filter.add(
                    new FilterWord(
                            text,
                            fullword
                    )
            );
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return false;
    }

    public boolean filterWordExists(String text) {
        try {
            ResultSet set = conn.prepareStatement("SELECT * FROM filter WHERE text='" + text + "'").executeQuery();
            if(set.next()) return true;
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        return false;
    }

    public void removeWordFromFilter(String text) {
        try {
            conn.prepareStatement("DELETE FROM filter WHERE text='" + text + "'").execute();
            filter.removeIf(filterWord -> filterWord.text.equals(text));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FilterWord> getWordsFromFilter() {
        return filter;
    }

    public Boolean checkMessage(String text) {
        text = " " + text + " ";
        AtomicReference<Boolean> safe = new AtomicReference<>(true);
        String finalText = text;
        filter.forEach(filterWord -> {
            if(filterWord.fullword && finalText.contains(" " + filterWord.text + " ")) safe.set(false);
            else if(!filterWord.fullword && finalText.contains(filterWord.text)) safe.set(false);
        });
        return safe.get();
    }

    public static class FilterWord {
        public final String text;
        public final Boolean fullword;
        public FilterWord(String text, Boolean fullword) {
            this.text = text;
            this.fullword = fullword;
        }
    }

}
