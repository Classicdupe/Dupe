package xyz.prorickey.classicdupe.playerevents.maze;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.MAZEmanager;

//make it so its a listener to wait for a player to be at a certain cord
@SuppressWarnings("unchecked")
public class PlayerOnEnd implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (MAZEmanager.MazeRunning && isInMaze(playerLocation) && isStandingOnGoldBlock(playerLocation)) {
            System.out.println("Running bs");
            player.sendMessage(Utils.format("<gold><b>You have completed the maze!"));

            MAZEmanager.leaderboard.add(player.getName());

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(Utils.format("<gold><b>" + player.getName() + " has completed the maze!"));
            }

            player.teleport(Bukkit.getWorld(player.getWorld().getName()).getSpawnLocation());
        }
    }

    private boolean isInMaze(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= MAZEmanager.MazeLocation.x - MAZEmanager.MazeSize && x <= MAZEmanager.MazeLocation.x + MAZEmanager.MazeSize &&
                y >= MAZEmanager.MazeLocation.y - 10 && y <= MAZEmanager.MazeLocation.y + 10 &&
                z >= MAZEmanager.MazeLocation.z - MAZEmanager.MazeSize && z <= MAZEmanager.MazeLocation.z + MAZEmanager.MazeSize;
    }

    private boolean isStandingOnGoldBlock(Location location) {
        Location blockunder = location.subtract(0, 1, 0);
        return blockunder.getBlock().getType() == Material.GOLD_BLOCK;
    }
}