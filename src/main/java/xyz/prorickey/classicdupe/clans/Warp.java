package xyz.prorickey.classicdupe.clans;

import org.bukkit.Location;

public class Warp {

    public String name;
    public Location location;
    public Integer level;

    public Warp(String name, Location loc, Integer level) {
        this.name = name;
        this.location = loc;
        this.level = level;
    }

}
