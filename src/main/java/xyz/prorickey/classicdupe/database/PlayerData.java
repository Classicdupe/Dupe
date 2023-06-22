package xyz.prorickey.classicdupe.database;

import org.bukkit.Bukkit;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    private final Connection conn;
    public final UUID uuid;
    public final String name;
    public String nickname;
    public long timesjoined;
    public long playtime;
    public boolean randomitem;
    public String chatcolor;
    public boolean gradient;
    public String gradientfrom;
    public String gradientto;
    public boolean night;
    public Integer balance;
    public boolean deathmessages;
    public boolean mutepings;
    public Integer killStreak;

    public PlayerData(Connection conn,
                      UUID uuid1,
                      String name1,
                      String nickname1,
                      long timesjoined1,
                      long playtime1,
                      boolean randomitem1,
                      String chatcolor1,
                      boolean gradient1,
                      String gradientfrom1,
                      String gradientto1,
                      boolean night1,
                      Integer balance1,
                      boolean deathmessages1,
                      boolean mutepings1,
                      Integer killStreak1
    ) {
        this.conn = conn;
        uuid = uuid1;
        name = name1;
        nickname = nickname1;
        timesjoined = timesjoined1;
        playtime = playtime1;
        randomitem = randomitem1;
        chatcolor = chatcolor1;
        gradient = gradient1;
        gradientfrom = gradientfrom1;
        gradientto = gradientto1;
        night = night1;
        balance = balance1;
        deathmessages = deathmessages1;
        mutepings = mutepings1;
        killStreak = killStreak1;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public long getTimesJoined() { return timesjoined; }
    public long getPlaytime() { return playtime; }
    public boolean isRandomItem() { return randomitem; }
    public String getChatColor() { return chatcolor; }
    public boolean isGradient() { return gradient; }
    public String getGradientFrom() { return gradientfrom; }
    public String getGradientTo() { return gradientto; }
    public boolean isNight() { return night; }
    public Integer getBalance() { return balance; }
    public boolean getDeathMessages() { return deathmessages; }
    public boolean getMutePings() { return mutepings; }
    public Integer getKillStreak() { return killStreak; }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET nickname=? WHERE uuid=?");
                stat.setString(1, nickname);
                stat.setString(2, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void resetNickname() {
        this.nickname = this.name;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET nickname=null WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void enableRandomItem() {
        this.randomitem = true;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET randomitem=true WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public Boolean swapRandomItem() {
        if(this.randomitem) {
            disableRandomItem();
            return false;
        } else {
            enableRandomItem();
            return true;
        }
    }

    public void disableRandomItem() {
        this.randomitem = false;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET randomitem=false WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setChatColor(String color) {
        this.chatcolor = color;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET chatcolor=? WHERE uuid=?");
                stat.setString(1, color);
                stat.setString(2, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public Boolean toggleGradient() {
        if(this.gradient) {
            disableGradient();
            return false;
        } else {
            enableGradient();
            return true;
        }
    }

    private void enableGradient() {
        this.gradient = true;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET gradient=TRUE WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    private void disableGradient() {
        this.gradient = false;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET gradient=FALSE WHERE uuid=?");
                stat.setString(1, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public ChatGradientCMD.GradientProfiles getGradientProfile() {
        return new ChatGradientCMD.GradientProfiles(
                this.gradientfrom,
                this.gradientto
        );
    }

    public void setGradientProfile(ChatGradientCMD.GradientProfiles profile) {
        this.gradientfrom = profile.gradientFrom;
        this.gradientto = profile.gradientTo;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET gradientFrom=? WHERE uuid=?");
                stat.setString(1, profile.gradientFrom);
                stat.setString(2, this.uuid.toString());
                stat.execute();
                PreparedStatement stat2 = conn.prepareStatement("UPDATE players SET gradientTo=? WHERE uuid=?");
                stat2.setString(1, profile.gradientTo);
                stat2.setString(2, this.uuid.toString());
                stat2.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setNightVision(boolean value) {
        this.night = value;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement stat = conn.prepareStatement("UPDATE players SET night=? WHERE uuid=?");
                stat.setBoolean(1, value);
                stat.setString(2, this.uuid.toString());
                stat.execute();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void subtractBalance(Integer sub) {
        this.balance -= sub;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET balance=balance-? WHERE uuid=?");
                statement.setInt(1, sub);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void addBalance(Integer add) {
        this.balance += add;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET balance=balance+? WHERE uuid=?");
                statement.setInt(1, add);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET balance=? WHERE uuid=?");
                statement.setInt(1, balance);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setDeathMessages(boolean deathMessages) {
        this.deathmessages = deathMessages;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET deathMessages=? WHERE uuid=?");
                statement.setBoolean(1, deathMessages);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setMutePings(boolean mutePings) {
        this.mutepings = mutePings;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET mutepings=? WHERE uuid=?");
                statement.setBoolean(1, mutePings);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void setKillStreak(Integer killStreak) {
        this.killStreak = killStreak;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET killstreak=? WHERE uuid=?");
                statement.setInt(1, killStreak);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

    public void addKillStreak(Integer add) {
        this.killStreak += add;
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            try {
                PreparedStatement statement = conn.prepareStatement("UPDATE players SET killstreak=killstreak+? WHERE uuid=?");
                statement.setInt(1, add);
                statement.setString(2, this.uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.toString());
            }
        });
    }

}
