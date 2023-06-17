package xyz.prorickey.classicdupe.clans.builders;

import org.bukkit.Location;

public class Warp {

    public final String name;
    public final Location location;
    public final Integer level;

    public Warp(String name, Location loc, Integer level) {
        this.name = name;
        this.location = loc;
        this.level = level;
    }

}
