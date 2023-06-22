package xyz.prorickey.classicdupe.playerevents.arena.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.playerevents.ARENAManager;
import xyz.prorickey.classicdupe.playerevents.arena.events.ArenaFunctions;

import java.util.ArrayList;

public class ArenaCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length==2){
            Player p = Bukkit.getServer().getPlayer(args[1]);
            Player t = (Player) sender;
            if(p==null || !p.isOnline()){ sender.sendMessage(Utils.cmdMsg("<red>Player not found! Are they offline?")); return true; }
            if(args[0].equalsIgnoreCase("decline")) {
                sender.sendMessage(Utils.cmdMsg("<yellow>Duel invitation from <italic>"+p.getName()+" <yellow>declined!"));
                p.sendMessage(Utils.cmdMsg("<yellow>Duel invitation to <italic>"+t.getName()+" <yellow>was declined!"));
                ArenaFunctions.removeDuelList(p, t);
                return true;
            }

            int listIndex = -1;
            for(int i=0;i< ArenaFunctions.getAttackerList().size();i++){
                if(ArenaFunctions.getAttackerList().get(i).equals(p)&&ArenaFunctions.getVictimList().get(i).equals(t)){
                    listIndex = i;
                }
            }
            if(listIndex==-1) { sender.sendMessage(Utils.cmdMsg("<yellow>Duel invitation from user <italic>"+p.getName()+" <yellow>not found!")); return true; }
            sender.sendMessage(Utils.cmdMsg("<yellow>Duel accepted! Teleporting now..."));
            t.sendMessage(Utils.cmdMsg("<yellow>Duel accepting! Teleporting now..."));
            ArenaFunctions.incArenaCount();
            ARENAManager.startDuel(p, t);
            return true;
        }

        if(args.length!=1){
            sender.sendMessage(Utils.cmdMsg("<yellow>To start a duel, run <italic>/duel <Player Name><yellow>!")); return true;
        }
        if(!(sender instanceof Player)){
            Bukkit.getLogger().info("Console cannot duel!"); return true;
        }
        Player p = (Player) sender;
        Player t = Bukkit.getServer().getPlayer(args[0]);
        if(t==null || !t.isOnline()) {
            sender.sendMessage(Utils.cmdMsg("<red>Player not found! Are they offline?")); return true;
        }
        ArenaFunctions.addAttackerList(p);
        ArenaFunctions.addVictimList(t);
        t.sendMessage(Utils.cmdMsg("<yellow><bold>"+p.getName()+" <yellow>has invited you to duel! <lime><italic>/duel accept"+p.getName()+" <red><italic>/duel decline "+p.getName()));
        p.sendMessage(Utils.cmdMsg("<yellow>Duel invite sent to <italic>"+t.getName()));
        return true;

    }
}
