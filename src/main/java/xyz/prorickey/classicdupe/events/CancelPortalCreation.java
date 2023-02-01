package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class CancelPortalCreation implements Listener {

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        e.setCancelled(true);
    }

}
