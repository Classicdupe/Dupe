package xyz.prorickey.classicdupe.playerevents.arena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class ArenaFunctions implements Listener {

    private static ArrayList<Player> attackerDuelList;
    private static ArrayList<Player> victimDuelList;
    private static int arenaCount;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player dPlayer = e.getPlayer();
        if(!(attackerDuelList.contains(dPlayer)&&victimDuelList.contains(dPlayer))){ return; }
        Player wPlayer = null;
        for(int i=0;i<attackerDuelList.size();i++){
            if(attackerDuelList.get(i)==dPlayer) { wPlayer = victimDuelList.get(i); removeVictimList(i); removeAttackerList(i); }
            if(victimDuelList.get(i)==dPlayer) { wPlayer = attackerDuelList.get(i); removeVictimList(i); removeAttackerList(i); }
        }
        // Add 1 to wPlayer's duel wins or whatever idk your database
        // Add 1 to dPlayer's duel loses
        // also if this fucks up death handling then it probably needs to add to regular deaths

        // Deload arena, tp wPlayer back to spawn

    }

    public static int getArenaCount(){
        return arenaCount;
    }

    public static void incArenaCount(){
        arenaCount++;
    }

    public static ArrayList<Player> getAttackerList(){
        return attackerDuelList;
    }

    public static ArrayList<Player> getVictimList(){
        return victimDuelList;
    }

    public static void removeAttackerList(int i){
        attackerDuelList.remove(i);
    }

    public static void removeDuelList(Player a, Player v){
        if(!(attackerDuelList.contains(a)&&victimDuelList.contains(v))) { return; }
        for(int i=0;i<attackerDuelList.size();i++){
            if(attackerDuelList.get(i)==a&&victimDuelList.get(i)==v){
                removeVictimList(i);
                removeAttackerList(i);
            }
        }
    }

    public static void removeVictimList(int i){
        victimDuelList.remove(i);
    }

    public static void addAttackerList(Player p){
        attackerDuelList.add(p);
    }

    public static void addVictimList(Player p){
        victimDuelList.add(p);
    }

    public static int makeX(){
        return 500*((getArenaCount()-1)%10);
    }

    public static int makeZ(){
        return (getArenaCount()-1)/10;
    }

}
