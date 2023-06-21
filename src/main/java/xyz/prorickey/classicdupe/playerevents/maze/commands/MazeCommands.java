package xyz.prorickey.classicdupe.playerevents.maze.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.playerevents.MAZEmanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MazeCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("joinmaze")) {
            if (!MAZEmanager.MazeRunning) {
                sender.sendMessage(ChatColor.RED + "There is no maze running!");
                return false;
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Joining maze...");
                //teleoi=port to maze location
                Location place = MAZEmanager.MazeSpawn;
                ((Player) sender).teleport(place);
                sender.sendMessage(ChatColor.GREEN + "You have been teleported to the maze!");
                return true;
            }

        }

        if (command.getName().equals("maze")) {

            if (sender.hasPermission("mod.maze")) {

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /maze <start|stop|set|join|leave|reset|location>");
                    return false;
                }

                switch (args[0]) {
                    case "start":
                        sender.sendMessage(ChatColor.GREEN + "Starting maze...");
                        if (MAZEmanager.MazeRunning == true) {
                            sender.sendMessage(ChatColor.RED + "Maze is already running!");
                        } else {
                            MAZEmanager.MazeRunning = true;
                            MAZEmanager.start();
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.GRAY + "----------------------------");
                                p.sendMessage(ChatColor.GREEN + "A maze event has started!");
                                p.sendMessage(ChatColor.GREEN + "Type /joinmaze to join!");
                                p.sendMessage(ChatColor.GRAY + "----------------------------");
                            }
                        }
                        return true;

                    case "stop":
                        sender.sendMessage(ChatColor.GREEN + "Stopping maze...");

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendMessage(ChatColor.GRAY + "----------------------------");
                            p.sendMessage(ChatColor.RED + "The maze event is ending!");
                            p.sendMessage(ChatColor.RED + "The maze will AUTO delete");
                            p.sendMessage(ChatColor.RED + "in 30 seconds! DO /spawn");
                            p.sendMessage(ChatColor.GRAY + "----------------------------");
                        }


                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getLocation().getX() >= MAZEmanager.MazeLocation.x && p.getLocation().getX() <= MAZEmanager.MazeLocation.x + 100) {
                                if (p.getLocation().getY() >= MAZEmanager.MazeLocation.y && p.getLocation().getY() <= MAZEmanager.MazeLocation.y + 100) {
                                    if (p.getLocation().getZ() >= MAZEmanager.MazeLocation.z && p.getLocation().getZ() <= MAZEmanager.MazeLocation.z + 100) {
                                        p.teleport(ClassicDupe.getDatabase().getSpawn("hub"));
                                    }
                                }
                            }
                        }

                        MAZEmanager.MazeRunning = false;
                        MAZEmanager.end();


                        return true;
                    case "set":
                        sender.sendMessage(ChatColor.YELLOW + "Setting Maze Size to " + args[1] + "...");
                        MAZEmanager.MazeSize = Integer.parseInt(args[1]);

                        return true;
                    case "reset":
                        sender.sendMessage(ChatColor.YELLOW + "FEATURE WIP FOR V2");

                    case "location":

                        sender.sendMessage(ChatColor.GREEN + "Getting maze location...");
                        //get location of player
                        Location loc = ((Player) sender).getLocation();
                        String world = loc.getWorld().getName();

                        //set location of mazeManager to player location
                        MAZEmanager.MazeLocation = new Vector3d(loc.getX(), loc.getY(), loc.getZ());
                        MAZEmanager.MazeLoc = loc;


                        Plugin plugin = ClassicDupe.plugin;


                        if (!plugin.getDataFolder().exists()) {
                            plugin.getDataFolder().mkdirs();
                        }


                        Path path = Paths.get(plugin.getDataFolder().getAbsolutePath() + "/maze");
                        if (!path.toFile().exists()) {
                            path.toFile().mkdirs();
                        }


                        Path path2 = Paths.get(path.toFile().getAbsolutePath() + "/location.txt");
                        if (!path2.toFile().exists()) {
                            try {
                                path2.toFile().createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        String cords = MAZEmanager.MazeLocation.x + "," + MAZEmanager.MazeLocation.y + "," + MAZEmanager.MazeLocation.z;
                        try {
                            Files.write(path2, cords.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        sender.sendMessage(ChatColor.GREEN + "Maze location set to: " + MAZEmanager.MazeLocation.x + ", " + MAZEmanager.MazeLocation.y + ", " + MAZEmanager.MazeLocation.z);
                        return true;
                    case "setspawn":
                        sender.sendMessage(ChatColor.GREEN + "Setting spawn location...");

                        //get the location of the player
                        Location loc2 = ((Player) sender).getLocation();
                        MAZEmanager.MazeSpawn = loc2;


                        Plugin plugin2 = ClassicDupe.plugin;

                        File file = new File(plugin2.getDataFolder().getAbsolutePath() + "/maze/MazeSpawn.txt");


                        if (!file.exists()) {
                            sender.sendMessage(ChatColor.RED + "Error: Server Restart Required!");

                            return false;
                        }


                        try {
                            FileWriter writer = new FileWriter(file);
                            writer.write(loc2.getX() + "," + loc2.getY() + "," + loc2.getZ());
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        sender.sendMessage(ChatColor.GREEN + "Spawn location set to: " + loc2.getX() + ", " + loc2.getY() + ", " + loc2.getZ());
                        return true;


                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid arguments! Usage: /maze <setspawn|start|stop|set|reset|location>");
                        return true;

                }



            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }

        }

        return false;
    }
}
