package xyz.prorickey.classicdupe.playerevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3d;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MAZEmanager {

    //AUTO START
    public static boolean AutoStart = false;
    public static int BetweenAutoStarts = 60 * 60;


    //BASIC SETTINGS
    public static int CountDownTilStart = 30;
    public static int MaxLength = 15 * 60;

    //MAZE DATA
    public static int lastMazeRan = getDateNumber();
    public static boolean MazeRunning = false;
    public static Vector3d MazeLocation = new Vector3d(0, 0, 0);
    public static Location MazeLoc;
    public static int MazeSize = 30;
    public static ArrayList leaderboard = new ArrayList<String>();
    public static Location MazeSpawn;

    //THREAD HANDLERS
    public static boolean AutoManagerThread = true;
    public static Thread AMThread;


    public MAZEmanager() {
    }

    public static void init() {
        Path folder = Paths.get(ClassicDupe.plugin.getDataFolder() + "/maze");
        Path path = Paths.get(ClassicDupe.plugin.getDataFolder() + "/maze/location.txt");

        if (ClassicDupe.plugin.getDataFolder().exists() == false) {
            ClassicDupe.plugin.getDataFolder().mkdir();
        }

        if (folder.toFile().exists() == false) {
            folder.toFile().mkdir();
        }

        if (path.toFile().exists() == false) {
            try {
                path.toFile().createNewFile();
                //set internals to 0,0,0
                Files.writeString(path, "0,0,0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String data = "";
        try {
            data = Files.readString(path);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String[] dataSplit = data.split(",");
        if (dataSplit[0]=="0") {
            dataSplit[0] = "0";
        }
        if (dataSplit[1]=="1") {
            dataSplit[1] = "0";
        }
        if (dataSplit[2]=="0") {
            dataSplit[2] = "2";
        }
        MazeLocation.x = Float.parseFloat(dataSplit[0]);
        MazeLocation.y = Float.parseFloat(dataSplit[1]);
        MazeLocation.z = Float.parseFloat(dataSplit[2]);

        //check if MazeSpawn.txt exists
        Path path2 = Paths.get(ClassicDupe.plugin.getDataFolder() + "/maze/MazeSpawn.txt");
        if (path2.toFile().exists() == false) {
            try {
                path2.toFile().createNewFile();
                //set internals to 0,0,0
                Files.writeString(path2, "0,65,0");
                MazeSpawn = new Location(Bukkit.getWorld("maze"), 0, 65, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String data2 = "";
            try {
                data2 = Files.readString(path2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] dataSplit2 = data2.split(",");
            MazeSpawn = new Location(Bukkit.getWorld("maze"), Float.parseFloat(dataSplit2[0]), Float.parseFloat(dataSplit2[1]), Float.parseFloat(dataSplit2[2]));
        }



        MazeLoc = new Location(Bukkit.getWorld("maze"), MazeLocation.x, MazeLocation.y, MazeLocation.z);
        if (AutoStart) {

            //THREAD OF the method auto manager and the thread's var name is AMThread
            AMThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (AutoManagerThread) {
                        if (AutoManagerThread == false) {
                            AMThread.interrupt();
                            break;
                        }
                        AutoManager();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            if (AutoManagerThread) {
                AMThread.start();
            }



        }
    }

    //AUTO MANAGER
    public static void AutoManager() {

    }

    public static void start() {
        leaderboard.clear();
    }

    public static void end() {


        //make async thread to wait 30 seconds
        //then run finalend()
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("waiting 30 seconds");
                try {
                    Thread.sleep(1);
                    finalend();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();


    }


    public static void finalend() {
        System.out.println("finalend");
        try {
            Thread.sleep(1000*30);
            System.out.println("done waiting");

            if (MAZEmanager.leaderboard.size() == 0) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ChatColor.GRAY + "----------------------------");
                    p.sendMessage(ChatColor.YELLOW + "The maze event has ended!");
                    p.sendMessage(ChatColor.RED + "No one completed the maze!");
                    p.sendMessage(ChatColor.GRAY + "----------------------------");
                }
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ChatColor.GRAY + "----------------------------");
                    p.sendMessage(ChatColor.YELLOW + "The maze event has ended!");
                    p.sendMessage(ChatColor.RED + "Top 3 players:");

                    if (MAZEmanager.leaderboard.size() >= 1) {
                        p.sendMessage(ChatColor.GOLD + "#1 " + MAZEmanager.leaderboard.get(0));
                    }

                    if (MAZEmanager.leaderboard.size() >= 2) {
                        p.sendMessage(ChatColor.YELLOW + "#2 " + MAZEmanager.leaderboard.get(1));
                    } else {
                        p.sendMessage(ChatColor.YELLOW + "#2 No one");
                    }

                    if (MAZEmanager.leaderboard.size() >= 3) {
                        p.sendMessage(ChatColor.YELLOW + "#3 " + MAZEmanager.leaderboard.get(2));
                    } else {
                        p.sendMessage(ChatColor.YELLOW + "#3 No one");
                    }

                    p.sendMessage(ChatColor.GRAY + "----------------------------");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //GET SECONDS FROM TWO DATE NUMBERS
    public static int getSecondsFromTwoDateNumbers(int date1, int date2) {
        int seconds = 0;
        int difference = date1 - date2;
        if (difference > 0) {
            seconds = difference;
        } else {
            seconds = difference * -1;
        }
        return seconds;
    }

    //GET DATE NUMBER
    public static int getDateNumber() {
        int date = 0;
        date = (int) (System.currentTimeMillis() / 1000);
        return date;
    }
}
