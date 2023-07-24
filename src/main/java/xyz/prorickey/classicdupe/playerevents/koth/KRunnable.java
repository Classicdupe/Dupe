package xyz.prorickey.classicdupe.playerevents.koth;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.KOTHEventManager;

import java.util.*;

public class KRunnable extends BukkitRunnable {
    private static long redPointSafe = 0;
    private static long yellowPointSafe = 0;
    private static long greenPointSafe = 0;
    public static final Map<Player, BossBar> bossBars = new HashMap<>();

    @Override
    public void run() {
        List<Player> redPoints = new ArrayList<>();
        List<Player> yellowPoints = new ArrayList<>();
        List<Player> greenPoints = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if(KOTHEventManager.region.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                switch(p.getLocation().getBlock().getType()) {
                    case RED_CARPET -> redPoints.add(p);
                    case YELLOW_CARPET -> yellowPoints.add(p);
                    case GREEN_CARPET -> greenPoints.add(p);
                    case PINK_CARPET -> p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30*20, 2));
                    case ORANGE_CARPET -> p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30*20, 2));
                    default -> {
                        if(!bossBars.containsKey(p)) {
                            Component text = Utils.format("KOTH Event");
                            BossBar bar = BossBar.bossBar(text, 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
                            p.showBossBar(bar);
                            bossBars.put(p, bar);
                        } else bossBars.get(p)
                                    .name(Utils.format("KOTH Event"))
                                    .color(BossBar.Color.YELLOW)
                                    .progress(1f)
                                    .overlay(BossBar.Overlay.PROGRESS);
                    }
                }
            }
        });

        if(redPoints.size() > 1) {
            redPointSafe = 0;
            redPoints.forEach(p -> bossBars.get(p)
                    .name(Utils.format("Hill Under Attack"))
                    .color(BossBar.Color.RED)
                    .overlay(BossBar.Overlay.PROGRESS));
        } else if(redPoints.size() == 1) {
            Player topDawg = redPoints.get(0);
            if(redPointSafe == 0) redPointSafe = System.currentTimeMillis();
            if((redPointSafe+15000) > System.currentTimeMillis()) bossBars.get(topDawg)
                        .name(Utils.format("Time Until Point: " + Math.round(((redPointSafe+15000)-System.currentTimeMillis())/1000.0)))
                        .progress(Float.parseFloat(String.valueOf((((redPointSafe+15000)-System.currentTimeMillis())/1000.0)/15.0)))
                        .color(BossBar.Color.YELLOW)
                        .overlay(BossBar.Overlay.PROGRESS);
            else {
                bossBars.get(topDawg)
                        .name(Utils.format("Time Until Point: 0"))
                        .color(BossBar.Color.YELLOW)
                        .overlay(BossBar.Overlay.PROGRESS);
                topDawg.playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1f));
                KOTHEventManager.PlayerKothData data = KOTHEventManager.getPlayerKothData(topDawg);
                data.addPoints(3);
                redPointSafe = System.currentTimeMillis();
            }
        } else redPointSafe = 0;

        if(yellowPoints.size() > 1) {
            yellowPointSafe = 0;
            yellowPoints.forEach(p -> bossBars.get(p)
                    .name(Utils.format("Hill Under Attack"))
                    .color(BossBar.Color.RED)
                    .overlay(BossBar.Overlay.PROGRESS));
        } else if(yellowPoints.size() == 1) {
            Player topDawg = yellowPoints.get(0);
            if(yellowPointSafe == 0) yellowPointSafe = System.currentTimeMillis();
            if((yellowPointSafe+15000) > System.currentTimeMillis()) bossBars.get(topDawg)
                        .name(Utils.format("Time Until Point: " + Math.round(((yellowPointSafe+15000)-System.currentTimeMillis())/1000.0)))
                        .color(BossBar.Color.YELLOW)
                        .progress(Float.parseFloat(String.valueOf((((yellowPointSafe+15000)-System.currentTimeMillis())/1000.0)/15.0)))
                        .overlay(BossBar.Overlay.PROGRESS);
            else {
                bossBars.get(topDawg)
                        .name(Utils.format("Time Until Point: 0"))
                        .color(BossBar.Color.YELLOW)
                        .overlay(BossBar.Overlay.PROGRESS);
                topDawg.playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1f));
                KOTHEventManager.PlayerKothData data = KOTHEventManager.getPlayerKothData(topDawg);
                data.addPoints(2);
                yellowPointSafe = System.currentTimeMillis();
            }
        } else yellowPointSafe = 0;

        if(greenPoints.size() > 1) {
            greenPointSafe = 0;
            greenPoints.forEach(p -> bossBars.get(p)
                    .name(Utils.format("Hill Under Attack"))
                    .color(BossBar.Color.RED)
                    .overlay(BossBar.Overlay.PROGRESS));
        } else if(greenPoints.size() == 1) {
            Player topDawg = greenPoints.get(0);
            if(greenPointSafe == 0) greenPointSafe = System.currentTimeMillis();
            if((greenPointSafe+15000) > System.currentTimeMillis()) bossBars.get(topDawg)
                        .name(Utils.format("Time Until Point: " + Math.round(((greenPointSafe+15000)-System.currentTimeMillis())/1000.0)))
                        .color(BossBar.Color.YELLOW)
                        .progress(Float.parseFloat(String.valueOf((((greenPointSafe+15000)-System.currentTimeMillis())/1000.0)/15.0)))
                        .overlay(BossBar.Overlay.PROGRESS);
            else {
                bossBars.get(topDawg)
                        .name(Utils.format("Time Until Point: 0"))
                        .color(BossBar.Color.YELLOW)
                        .overlay(BossBar.Overlay.PROGRESS);
                topDawg.playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1f));
                KOTHEventManager.PlayerKothData data = KOTHEventManager.getPlayerKothData(topDawg);
                data.addPoints(1);
                greenPointSafe = System.currentTimeMillis();
            }
        } else greenPointSafe = 0;

    }

}
