package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.prorickey.classicdupe.ClassicDupe;

public class Stats implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getEntity().getUniqueId().toString());
        if(e.getEntity().getKiller() != null) ClassicDupe.getDatabase().getPlayerDatabase().addKill(e.getEntity().getKiller().getUniqueId().toString());
    }

}
