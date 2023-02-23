package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.prorickey.classicdupe.ClassicDupe;

public class VoidTeleport implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getPlayer().getLocation().getY() < -64) {
            e.getPlayer().teleport(ClassicDupe.getDatabase().spawn);
        }
    }

}
