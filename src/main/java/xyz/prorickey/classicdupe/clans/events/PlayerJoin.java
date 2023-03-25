package xyz.prorickey.classicdupe.clans.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.prorickey.classicdupe.clans.ClansDatabase;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ClansDatabase.createIfNotExists(event.getPlayer());
    }

}
