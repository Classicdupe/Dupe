package xyz.prorickey.classicdupe.metrics;

import org.bukkit.plugin.java.JavaPlugin;

public class Metrics {

    public static ServerMetrics serverMetrics = null;
    public static PlayerMetrics playerMetrics = null;

    public static void init(JavaPlugin plugin) {
        serverMetrics = new ServerMetrics();
        playerMetrics = new PlayerMetrics(plugin);
        plugin.getServer().getPluginManager().registerEvents(playerMetrics, plugin);
    }

    public static ServerMetrics getServerMetrics() { return serverMetrics; }
    public static PlayerMetrics getPlayerMetrics() { return playerMetrics; }

}
